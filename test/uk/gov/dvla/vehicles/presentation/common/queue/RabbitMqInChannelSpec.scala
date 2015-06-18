package uk.gov.dvla.vehicles.presentation.common.queue

import com.rabbitmq.client.{AMQP, Channel, Connection, Consumer, DefaultConsumer, Envelope, ShutdownSignalException}
import java.io.IOException
import org.mockito.Matchers.{any, eq => meq}
import org.mockito.Mockito.{atLeastOnce, verify, when}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfterAll
import play.api.libs.json.Json
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class RabbitMqInChannelSpec extends UnitSpec with BeforeAndAfterAll  {
  case class TB(name: String, value: Int, value1: Boolean)
  private implicit val jsonFormat = Json.format[TB]

  private val testQueue = "test-queue"
  private val testConsumerTag = "consumer-tag-1"

  "subscribe" should {
    "get a connection, create channel and register a consumer as well as close them on close()" in {
      val (channel, connection, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => testConsumerTag)

      val inChannel = factory.subscribe[TB]("test-queue", tb => Future.successful(MessageAck.Ack))

      verify(connection).createChannel()
      verify(channel).basicConsume(meq(testQueue), meq(false), any[Consumer])

      when(channel.basicCancel(any[String])).thenThrow(new IOException())
      when(channel.close()).thenThrow(new IOException())
      when(connection.close()).thenThrow(new IOException())

      inChannel.map(_.close())
      verify(channel).basicCancel(testConsumerTag)
      verify(channel).close()
      verify(connection).close()
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

    "notify message reception in the correct order" ignore {
      val event1 = TB("event-1-с български букви", 1, value1 = true)
      val event2 = TB("event-2  με ελληνικά", 2, value1 = true)
      val event3 = TB("event-3 希腊", 3, value1 = true)
      val (channel, _, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.message(event1)
        c.message(event2)
        c.message(event3)
        testConsumerTag
      })

      val messages = new ArrayBuffer[TB]()

      factory.subscribe[TB]("test-queue", tb => {
        messages += tb
        Future.successful(MessageAck.Ack)
      })
      verify(channel).basicConsume(meq("test-queue"), meq(false), any[Consumer])
      messages.toSeq should equal(Seq(event1, event2, event3))
    }

    "un-parsable json messages should be put in the dead letter queue" in {
      val (channel, _, _, _, factory, stubBasicConsumer) = setup
      val envelope1 = mock[Envelope]
      val envelope2 = mock[Envelope]
      when(envelope1.getDeliveryTag).thenReturn(65471L)
      when(envelope2.getDeliveryTag).thenReturn(65472L)
      stubBasicConsumer(c => {
        c.handleDelivery(
          testConsumerTag,
          envelope1,
          mock[AMQP.BasicProperties],
          "Not parsable json".getBytes
        )
        c.handleDelivery(
          testConsumerTag,
          envelope2,
          mock[AMQP.BasicProperties],
          """{"some-name":"some-value"}""".getBytes
        )
        testConsumerTag
      })

      val messages = new ArrayBuffer[TB]()

      factory.subscribe[TB]("test-queue", tb => {
        messages += tb
        Future.successful(MessageAck.Ack)
      })

      verify(channel).basicReject(envelope1.getDeliveryTag, false)
      verify(channel).basicReject(envelope2.getDeliveryTag, false)

      messages should be(empty)
    }

    "Acknowledge the message on successful processing of the message" ignore {
      val event1 = TB("event-1-с български букви", 1, value1 = true)
      val event2 = TB("event-2  με ελληνικά", 2, value1 = true)
      val event3 = TB("event-3 希腊", 3, value1 = true)
      val (channel, _, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.message(event1, 1)
        c.message(event2, 2)
        c.message(event3, 3)
        testConsumerTag
      })

      val messages = new ArrayBuffer[TB]()

      factory.subscribe[TB]("test-queue", tb => {
        messages += tb
        Future.successful(MessageAck.Ack)
      })

      verify(channel).basicAck(1, false)
      verify(channel).basicAck(2, false)
      verify(channel).basicAck(3, false)

      messages.toSeq should equal(Seq(event1, event2, event3))
    }

    "Negative acknowledge the message on failure processing of the message" ignore {
      val event1 = TB("event-1-с български букви", 1, value1 = true)
      val event2 = TB("event-2  με ελληνικά", 2, value1 = true)
      val event3 = TB("event-3 希腊", 3, value1 = true)
      val (channel, _, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.message(event1, 1)
        c.message(event2, 2)
        c.message(event3, 3)
        testConsumerTag
      })

      val messages = new ArrayBuffer[TB]()

      factory.subscribe[TB]("test-queue", tb => {
        messages += tb
        Future.successful(MessageAck.Nack)
      })

      verify(channel).basicNack(1, false, true)
      verify(channel).basicNack(2, false, true)
      verify(channel).basicNack(3, false, true)

      messages.toSeq should equal(Seq(event1, event2, event3))
    }

    "Negative acknowledge the message on message processing failure" ignore {
      val event1 = TB("event-1-с български букви", 1, value1 = true)
      val event2 = TB("event-2  με ελληνικά", 2, value1 = true)
      val event3 = TB("event-3 希腊", 3, value1 = true)
      val (channel, _, _, _, factory, stubBasicConsumer) = setup
      stubBasicConsumer(c => {
        c.message(event1, 1)
        c.message(event2, 2)
        c.message(event3, 3)
        testConsumerTag
      })

      val messages = new ArrayBuffer[TB]()

      factory.subscribe[TB]("test-queue", tb => {
        messages += tb
        Future.failed(new IllegalStateException("Boooooom"))
      })

      verify(channel).basicNack(1, false, true)
      verify(channel).basicNack(2, false, true)
      verify(channel).basicNack(3, false, true)

      messages.toSeq should equal(Seq(event1, event2, event3))
    }
  }

  private def setup = {
    val channel = mock[Channel]
    val connection = mock[Connection]
    val rabbitFactory = mock[RabbitMqConnectionFactory]
    val consumer = mock[DefaultConsumer]
    val factory = new RabbitMqChannelFactory(rabbitFactory)

    val stubBasicConsumer = (f:DefaultConsumer => String) =>
      when(channel.basicConsume(meq(testQueue), meq(false), any[Consumer])).thenAnswer(new Answer[String] {
        override def answer(invocation: InvocationOnMock): String = {
          val consumer = invocation.getArguments()(2).asInstanceOf[DefaultConsumer]
          consumer.handleConsumeOk(testConsumerTag)
          f(consumer)
        }
      })

    when(consumer.getConsumerTag).thenReturn(testConsumerTag)
    when(rabbitFactory.connection).thenReturn(connection)
    when(connection.createChannel()).thenReturn(channel)

    (channel, connection, rabbitFactory, consumer, factory, stubBasicConsumer)
  }

  implicit class RichConsumer(c: DefaultConsumer) {
    def message(tb: TB, deliveryTag: Long = -1): Unit = {
      val envelope = mock[Envelope]
      when(envelope.getDeliveryTag).thenReturn(deliveryTag)
      c.handleDelivery(
        testConsumerTag,
        envelope,
        mock[AMQP.BasicProperties],
        jsonFormat.writes(tb).toString().getBytes
      )
    }
  }
}
