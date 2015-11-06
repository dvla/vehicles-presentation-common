package uk.gov.dvla.vehicles.presentation.common.queue

import com.rabbitmq.client.{AMQP, Channel, Connection}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => meq}
import org.mockito.Mockito.{when, verify}
import play.api.libs.json.Json
import RMqPriority.toRMqPriority
import scala.util.{Failure, Success}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class RabbitMqOutChannelSpec extends UnitSpec {
  case class TB(name: String, value: Int, value1: Boolean)
  private val jsonFormat = Json.format[TB]
  val message1 = TB("event-1-с български букви", 1, value1 = true)
  val message2 = TB("event-2  με ελληνικά", 2, value1 = true)
  val message3 = TB("event-3 希腊", 3, value1 = true)
  val queueName = "test-queue"

  "put" should {
    "Create a channel and a durable queue if it doesn't exist" in {
      val (rmqChannel, connection, rabbitFactory, factory) = setup
      factory.outChannel(queueName)
      verify(rabbitFactory).connection
      verify(connection).createChannel()
      verify(rmqChannel).queueDeclare(queueName, true, false, false, null)
    }

    "send a persistent message with Normal priority by default" in {
      val (rmqChannel, _, _, factory) = setup
      val channel = factory.outChannel[TB](queueName)
      channel.map(_.put(message1)(jsonFormat))

      val properties = new AMQP.BasicProperties.Builder()
        .contentType("application/json")
        .deliveryMode(DeliveryMode.Persistent)
        .priority(toRMqPriority(Priority.Normal))
        .build()

      val exchangeArg = ArgumentCaptor.forClass(classOf[String])
      val routingArg = ArgumentCaptor.forClass(classOf[String])
      val propertiesArg = ArgumentCaptor.forClass(classOf[AMQP.BasicProperties])
      val messageArg = ArgumentCaptor.forClass(classOf[Array[Byte]])

      verify(rmqChannel).basicPublish(
        exchangeArg.capture(),
        routingArg.capture(),
        propertiesArg.capture(),
        messageArg.capture()
      )

      exchangeArg.getValue should equal("")
      routingArg.getValue should equal(queueName)
      propertiesArg.getValue.toString should equal(properties.toString)
      messageArg.getValue should equal(jsonFormat.writes(message1).toString().getBytes)
    }

    "send messages.en with explicit priority if specified" in {
      val (rmqChannel, _, _, factory) = setup
      val channel = factory.outChannel[TB](queueName)
      channel.map(_.put(message1, Priority.Low)(jsonFormat))

      val properties = new AMQP.BasicProperties.Builder()
        .contentType("application/json")
        .deliveryMode(DeliveryMode.Persistent)
        .priority(toRMqPriority(Priority.Low))
        .build()

      val exchangeArg = ArgumentCaptor.forClass(classOf[String])
      val routingArg = ArgumentCaptor.forClass(classOf[String])
      val propertiesArg = ArgumentCaptor.forClass(classOf[AMQP.BasicProperties])
      val messageArg = ArgumentCaptor.forClass(classOf[Array[Byte]])

      verify(rmqChannel).basicPublish(
        exchangeArg.capture(),
        routingArg.capture(),
        propertiesArg.capture(),
        messageArg.capture()
      )

      exchangeArg.getValue should equal("")
      routingArg.getValue should equal(queueName)
      propertiesArg.getValue.toString should equal(properties.toString)
      messageArg.getValue should equal(jsonFormat.writes(message1).toString().getBytes)
    }

    "fail if cannot serialise the object" in {
      val (_, _, _, factory) = setup

      implicit val mockJsonWrites = mock[play.api.libs.json.Writes[TB]]
      when(mockJsonWrites.writes(any[TB])).thenThrow(new RuntimeException("Serialization failed in test"))

      val channel = factory.outChannel[TB](queueName)

      channel.map(_.put(message1)(mockJsonWrites)) match {
        case Failure(e:QueueException) => // works as expected
        case Failure(e) => fail("QueueException expected bug got:" + e)
        case _ => fail("QueueException expected but no exception was thrown")

      }
    }

    "fail if cannot queue" in {
      val (rmqChannel, _, _, factory) = setup
      when(rmqChannel.basicPublish(
        meq(""),
        meq(queueName),
        any[AMQP.BasicProperties],
        meq(jsonFormat.writes(message1).toString().getBytes)
      )).thenThrow(new RuntimeException("Boom"))

      val channel = factory.outChannel[TB](queueName)

      channel match {
        case Success(c) =>
          intercept[QueueException] {
            c.put(message1, Priority.Low)(jsonFormat)
          }
        case _ => fail("channel was failure")
      }
    }
  }

  "close" should {
    "Close the connection and the open rmqChannel" in {
      val (rmqChannel, connection, _, factory) = setup
      factory.outChannel[Any](queueName).map(_.close())
      verify(connection).close()
      verify(rmqChannel).close()
    }
  }

  private def setup = {
    val channel = mock[Channel]
    val connection = mock[Connection]
    val rabbitFactory = mock[RabbitMqConnectionFactory]
    val factory = new RabbitMqChannelFactory(rabbitFactory)

    when(rabbitFactory.connection).thenReturn(connection)
    when(connection.createChannel()).thenReturn(channel)

    (channel, connection, rabbitFactory, factory)
  }
}
