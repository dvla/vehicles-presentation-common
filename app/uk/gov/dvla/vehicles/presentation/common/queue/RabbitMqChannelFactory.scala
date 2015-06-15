package uk.gov.dvla.vehicles.presentation.common.queue

import java.io.IOException

import akka.actor.ActorSystem
import com.google.inject.Inject
import com.rabbitmq.client._
import play.api.libs.json.{Format, Json, Writes, Reads}

import scala.concurrent.Future

trait RabbitMqConfig {
  def rabbitMqVirtualHost: Option[String]
  def rabbitMqUserName: Option[String]
  def rabbitMqPassword: Option[String]
  def rabbitMqUseSsl: Boolean
  def rabbitMqNetworkRecoverIntervalMs: Option[Long]
  def rabbitMqHearthbeat: Option[Int]
  def rabbitMqAddresses: Array[Address]
}

class RabbitMqConnectionFactory @Inject()(config: RabbitMqConfig, factory: ConnectionFactory) {
  def connection: Connection = {
    config.rabbitMqVirtualHost.foreach(factory.setVirtualHost(_))
    config.rabbitMqUserName.foreach(factory.setUsername(_))
    config.rabbitMqPassword.foreach(factory.setPassword(_))
    config.rabbitMqHearthbeat.foreach(factory.setRequestedHeartbeat(_))
    if (config.rabbitMqUseSsl) factory.useSslProtocol()
    config.rabbitMqNetworkRecoverIntervalMs
      .fold(factory.setAutomaticRecoveryEnabled(false)) { interval =>
        factory.setAutomaticRecoveryEnabled(true)
        factory.setNetworkRecoveryInterval(interval)
      }

    factory.newConnection(config.rabbitMqAddresses)
  }
}

class RabbitMqInChannel[T] (connectionFactory: RabbitMqConnectionFactory,
                            queueName: String,
                            onNext: T => Future[MessageAck])
                           (implicit jsonReads: Reads[T],
                            actorSystem: ActorSystem) extends ClosableChannel {
  private val connection = connectionFactory.connection
  private val rabbitChannel = connection.createChannel()
  private val consumer = new DefaultConsumer(rabbitChannel) {
    @throws(classOf[IOException])
    override def handleDelivery(consumerTag: String,
                                envelope: Envelope,
                                properties: AMQP.BasicProperties,
                                body: Array[Byte]): Unit = {

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

  rabbitChannel.basicConsume(queueName, true, consumer)
}

class RabbitMqOutChannel[T] extends OutChannel[T] {
  @throws(classOf[QueueException])
  override def put(message: T, priority: Priority = Priority.Normal)
                  (implicit jsonWrite: Writes[T]): Unit = ???

  override def close(): Unit = ???
}


class RabbitMqChannelFactory @Inject()(connectionFactory: RabbitMqConnectionFactory)
                                   (implicit actorSystem: ActorSystem) extends ChannelFactory {

  override def outChannel[T](queue: String)(implicit jsonReads: Writes[T]): OutChannel[T] =
    new RabbitMqOutChannel[T]()

  override def subscribe[T](queue: String, onNext: T => Future[MessageAck])
                           (implicit jsonReads: Reads[T]): ClosableChannel =
    new RabbitMqInChannel[T](connectionFactory, queue, onNext)
}
