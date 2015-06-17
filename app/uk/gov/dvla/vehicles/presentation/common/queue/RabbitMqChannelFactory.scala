package uk.gov.dvla.vehicles.presentation.common.queue

import com.fasterxml.jackson.core.JsonParseException
import com.google.inject.Inject
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Address
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.ShutdownSignalException
import java.io.IOException
import play.api.libs.json.{Json, Reads, Writes}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Try, Failure, Success}
import uk.gov.dvla.vehicles.presentation.common.queue.RMqPriority.toRMqPriority

object DeliveryMode {
  final val Persistent = 2
}

object RMqPriority {
  def toRMqPriority(priority: Priority): Int = {
    Priorities.getOrElse(priority, 1)
  }
  final val Priorities = Map[Priority, Int](Priority.Normal -> 1, Priority.Low -> 0)
}

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
    config.rabbitMqVirtualHost.foreach(factory.setVirtualHost)
    config.rabbitMqUserName.foreach(factory.setUsername)
    config.rabbitMqPassword.foreach(factory.setPassword)
    config.rabbitMqHeartBeat.foreach(factory.setRequestedHeartbeat)
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
  private val rabbitChannel = try connection.createChannel() catch {
    case NonFatal(e) =>
      closeConnection()
      throw e
  }
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

  try rabbitChannel.basicConsume(queueName, false, consumer) catch {
    case NonFatal(e) =>
      closeChannel()
      closeConnection()
      throw e
  }

  override def close(): Unit = {
    cancelConsumer()
    closeChannel()
    closeConnection()
  }

  private def closeConnection(): Unit = {
    try connection.close() catch {
      case e: IOException => // do nothing
    }
  }

  private def closeChannel(): Unit = {
    try rabbitChannel.close() catch {
      case e: IOException => // do nothing
    }
  }

  private def cancelConsumer(): Unit = {
    try rabbitChannel.basicCancel(consumer.getConsumerTag) catch {
      case e: IOException => // do nothing
    }
  }
}

class RabbitMqOutChannel[T](connectionFactory: RabbitMqConnectionFactory,
                            queueName: String) extends OutChannel[T] {
  private val connection = connectionFactory.connection
  private val rabbitChannel = try connection.createChannel() catch {
    case NonFatal(e) =>
      closeConnection()
      throw e
  }

  try rabbitChannel.queueDeclare(queueName, true, false, false, null) catch {
    case NonFatal(e) =>
      close()
      throw e
  }

  @throws(classOf[QueueException])
  override def put(message: T, priority: Priority = Priority.Normal)
                  (implicit jsonWrite: Writes[T]): Unit = {
    val props = new AMQP.BasicProperties.Builder()
      .contentType("application/json")
      .deliveryMode(DeliveryMode.Persistent)
      .priority(toRMqPriority(priority))
      .build()

    try rabbitChannel.basicPublish("", queueName, props, jsonWrite.writes(message).toString().getBytes)
    catch {
      case e: Throwable =>println("Exception:" + e ); throw new QueueException(e)
    }
  }

  override def close(): Unit = {
    closeChannel()
    closeConnection()
  }

  private def closeChannel(): Unit = try rabbitChannel.close() catch {
    case e: IOException => // do nothing
  }

  private def closeConnection(): Unit = try connection.close() catch {
    case e: IOException => // do nothing
  }
}

class RabbitMqChannelFactory @Inject()(connectionFactory: RabbitMqConnectionFactory) extends ChannelFactory {

  override def outChannel[T](queueName: String): Try[OutChannel[T]] =
    Try(new RabbitMqOutChannel[T](connectionFactory, queueName))

  override def subscribe[T](queueName: String, onNext: T => Future[MessageAck])
                           (implicit jsonReads: Reads[T]): Try[ClosableChannel] =
    Try(new RabbitMqInChannel[T](connectionFactory, queueName, onNext))
}
