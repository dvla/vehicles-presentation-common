package uk.gov.dvla.vehicles.presentation.common.queue

import com.google.inject.Inject
import com.rabbitmq.client.Connection
import play.api.libs.json.{Json, Writes, Reads}

import scala.concurrent.Future

trait RabbitMqConfig {
  def host: String
  def virtualHost: String
  def port: Int
  def userName: String
  def pass: String
  def useSsl: Boolean
  def addresses: Seq[com.rabbitmq.client.Address]
}

class RabbitMqChanTrait[T](connection: Connection) extends InChannel[T] {
  private val rabbitChannel = connection.createChannel()

  override def subscribe(onNext: T => Future[MessageAck])
                        (implicit jsonReads: Reads[T]): Unit = {
    rabbitChannel.
  }//jsonReads.reads(Json.p)

  override def close(): Unit = {
    rabbitChannel.close()
    connection.close()
  }
}

class RabbitMqOutChan[T] extends OutChannel[T] {
  @throws(classOf[QueueException])
  override def put(message: T, priority: Priority = Priority.Normal)
                  (implicit jsonWrite: Writes[T]): Unit = ???

  override def close(): Unit = ???
}


class RabbitMqChanFactory @Inject()(config: RabbitMqConfig) extends ChannelFactory {

  override def channel(queue: String): Channel = ???

  override def outChannel(queue: String): OutChannel = ???

  override def inChannel(queue: String): InChannel = ???
}
