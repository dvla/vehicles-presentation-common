package uk.gov.dvla.vehicles.presentation.common.queue

import java.io.IOException

import akka.actor.ActorSystem
import com.rabbitmq.client._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfterAll
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import org.mockito.Mockito.{when, verify, atLeastOnce}
import org.mockito.Matchers.{any, eq => meq}

import scala.concurrent.Future

class RabbitMqInChannelSpec extends UnitSpec with BeforeAndAfterAll  {
  private case class TB(name: String, value: Int, value1: Boolean)
  private implicit val actorSystem = ActorSystem.create("testActorSystem")
  private implicit val r = Json.format[TB]
  private case class Config(rabbitMqVirtualHost: Option[String],
                            rabbitMqUserName: Option[String],
                            rabbitMqPassword: Option[String],
                            rabbitMqUseSsl: Boolean,
                            rabbitMqNetworkRecoverIntervalMs: Option[Long],
                            rabbitMqHearthbeat: Option[Int],
                            rabbitMqAddresses: Array[com.rabbitmq.client.Address]) extends RabbitMqConfig
  private val config = Config(rabbitMqVirtualHost = None,
    rabbitMqUserName = None,
    rabbitMqPassword = None,
    rabbitMqUseSsl = false,
    rabbitMqNetworkRecoverIntervalMs = None,
    rabbitMqHearthbeat = None,
    rabbitMqAddresses = Array.empty[com.rabbitmq.client.Address])
  private val testQueue = "test-queue"
  private val testConsumerTag = "consumer-tag-1"

  override def afterAll(): Unit = actorSystem.shutdown()

  "subscribe" should {
    "get a connection, create channel and register a consumer as well as close them on close()" in {
      val (channel, connection, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => testConsumerTag)

      val inChannel = factory.subscribe[TB]("test-queue", tb => Future.successful(MessageAck.Ack))

      verify(connection).createChannel()
      verify(channel).basicConsume(meq(testQueue), meq(true), any[Consumer])

      when(channel.basicCancel(any[String])).thenThrow(new IOException())
      when(channel.close()).thenThrow(new IOException())
      when(connection.close()).thenThrow(new IOException())

      inChannel.close()
      verify(channel).basicCancel(testConsumerTag)
      verify(channel).close()
      verify(connection).close()
    }

    "notify message reception in the correct order" in {
      val (_, _, _, _, _, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
//        c.handleDelivery()
        testConsumerTag
      })
    }

    "call close() on handleClose" in {
      val (channel, connection, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.handleCancel(testConsumerTag)
        testConsumerTag
      })

      factory.subscribe[TB]("test-queue", tb => Future.successful(MessageAck.Ack))

      verify(channel, atLeastOnce).basicCancel(testConsumerTag)
      verify(channel, atLeastOnce).close()
      verify(connection, atLeastOnce).close()
    }

    "call close() on handleShutdown" in {
      val (channel, connection, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.handleShutdownSignal(testConsumerTag, mock[ShutdownSignalException])
        testConsumerTag
      })

      factory.subscribe[TB]("test-queue", tb => Future.successful(MessageAck.Ack))

      verify(channel, atLeastOnce).basicCancel(testConsumerTag)
      verify(channel, atLeastOnce).close()
      verify(connection, atLeastOnce).close()
    }

    "Acknowledge the message on successful processing of the message" in {

    }

    "Negative acknowledge the message on failure processing of the message" in {

    }

    "Negative acknowledge the message on message processing timeout" in {

    }
  }

  def setup = {
    val channel = mock[Channel]
    val connection = mock[Connection]
    val rabbitFactory = mock[RabbitMqConnectionFactory]
    val consumer = mock[DefaultConsumer]
    val factory = new RabbitMqChannelFactory(rabbitFactory)

    val stubBasicConsumer = (f:DefaultConsumer => String) =>
      when(channel.basicConsume(meq(testQueue), meq(true), any[Consumer])).thenAnswer(new Answer[String] {
        override def answer(invocation: InvocationOnMock): String = {
          val consumer = invocation.getArguments()(2).asInstanceOf[DefaultConsumer]
          consumer.handleConsumeOk(testConsumerTag)
          f(consumer)
        }
      })

//    def stubBasicConsumer(f: DefaultConsumer => String): Unit = {
//      when(channel.basicConsume(meq(testQueue), meq(true), any[Consumer])).thenAnswer(new Answer[String] {
//        override def answer(invocation: InvocationOnMock): String =
//          f(invocation.getArguments()(2).asInstanceOf[DefaultConsumer])
//      })
//    }

    when(consumer.getConsumerTag).thenReturn(testConsumerTag)
    when(rabbitFactory.connection).thenReturn(connection)
    when(connection.createChannel()).thenReturn(channel)

    (channel, connection, rabbitFactory, consumer, factory, stubBasicConsumer)
  }
}
