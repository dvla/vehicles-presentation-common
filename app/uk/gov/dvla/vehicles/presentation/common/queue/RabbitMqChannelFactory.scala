package uk.gov.dvla.vehicles.presentation.common.queue

import java.io.IOException

import akka.actor.ActorSystem
import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import com.rabbitmq.client._
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal
import scala.util.{Try, Failure, Success}

trait RabbitMqConfig {
  def rabbitMqVirtualHost: Option[String]
  def rabbitMqUserName: Option[String]
  def rabbitMqPassword: Option[String]
  def rabbitMqUseSsl: Boolean
  def rabbitMqNetworkRecoverIntervalMs: Option[Long]
  def rabbitMqHeartBeat: Option[Int]
  def rabbitMqAddresses: Array[Address]
}

class RabbitMqConnectionFactory @Inject()(config: RabbitMqConfig, factory: ConnectionFactory) {
  def connection: Connection = {
    config.rabbitMqVirtualHost.foreach(factory.setVirtualHost(_))
    config.rabbitMqUserName.foreach(factory.setUsername(_))
    config.rabbitMqPassword.foreach(factory.setPassword(_))
    config.rabbitMqHeartBeat.foreach(factory.setRequestedHeartbeat(_))
    if (config.rabbitMqUseSsl) factory.useSslProtocol()
    config.rabbitMqNetworkRecoverIntervalMs
      .fold(factory.setAutomaticRecoveryEnabled(false)) { interval =>
        factory.setAutomaticRecoveryEnabled(true)
        factory.setNetworkRecoveryInterval(interval)
      }

    factory.newConnection(config.rabbitMqAddresses)
  }
}

class RabbitMqInChannel[T](connectionFactory: RabbitMqConnectionFactory,
                            queueName: String,
                            onNext: T => Future[MessageAck])
                           (implicit jsonReads: Reads[T]) extends ClosableChannel {
  private val connection = connectionFactory.connection
  private val rabbitChannel = connection.createChannel()
  private val consumer = new DefaultConsumer(rabbitChannel) {
    @throws(classOf[IOException])
    override def handleDelivery(consumerTag: String,
                                envelope: Envelope,
                                properties: AMQP.BasicProperties,
                                body: Array[Byte]): Unit = {
      Try(jsonReads.reads(Json.parse(body))).map(
        _.fold (
          validationErrors => rabbitChannel.basicReject(envelope.getDeliveryTag, false),
          result => onNext(result).onComplete {
            case Success(MessageAck.Ack) =>
              rabbitChannel.basicAck(envelope.getDeliveryTag, false)
            case Success(MessageAck.Nack) =>
              rabbitChannel.basicNack(envelope.getDeliveryTag, false, true)
            case Failure(e) =>
              rabbitChannel.basicNack(envelope.getDeliveryTag, false, true)
          }
        )
      ).recover{
        case e:JsonParseException => rabbitChannel.basicReject(envelope.getDeliveryTag, false)
      }
    }

    override def handleCancel(consumerTag: String): Unit = close()

    override def handleShutdownSignal(consumerTag: String, sig: ShutdownSignalException): Unit = close()
  }


  override def close(): Unit = {
    try rabbitChannel.basicCancel(consumer.getConsumerTag) catch {
      case e: IOException => // do nothing
    }
    try rabbitChannel.close() catch {
      case e: IOException => // do nothing
    }
    try connection.close() catch {
      case e: IOException => // do nothing
    }
  }

  rabbitChannel.basicConsume(queueName, false, consumer)
}

class RabbitMqOutChannel[T](connectionFactory: RabbitMqConnectionFactory) extends OutChannel[T] {
  private val connection = connectionFactory.connection
  private val rabbitChannel = connection.createChannel()

  @throws(classOf[QueueException])
  override def put(message: T, priority: Priority = Priority.Normal)
                  (implicit jsonWrite: Writes[T]): Unit = {
  }

  override def close(): Unit = {
    try rabbitChannel.close() catch {
      case e: IOException => // do nothing
    }
    try connection.close() catch {
      case e: IOException => // do nothing
    }
  }
}


class RabbitMqChannelFactory @Inject()(connectionFactory: RabbitMqConnectionFactory) extends ChannelFactory {

  override def outChannel[T](queue: String)(implicit jsonReads: Writes[T]): OutChannel[T] =
    new RabbitMqOutChannel[T](connectionFactory)

  override def subscribe[T](queue: String, onNext: T => Future[MessageAck])
                           (implicit jsonReads: Reads[T]): ClosableChannel =
    new RabbitMqInChannel[T](connectionFactory, queue, onNext)
}
