package uk.gov.dvla.vehicles.presentation.common.queue

import com.rabbitmq.client._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => meq}
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.libs.json.Json
import RMqPriority.toRMqPriority
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

import scala.util.Success

class RabbitMqOutChannelSpec extends UnitSpec {
  case class TB(name: String, value: Int, value1: Boolean)
  private implicit val jsonFormat = Json.format[TB]
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
      val channel = factory.outChannel(queueName)
      channel.map(_.put(message1))

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

    "send messages with explicit priority if specified" in {
      val (rmqChannel, _, _, factory) = setup
      val channel = factory.outChannel(queueName)
      channel.map(_.put(message1, Priority.Low))

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

    "fail if cannot serialise the object" ignore {
      fail("Not implemented")
      val (rmqChannel, _, _, factory) = setup
      val channel = factory.outChannel(queueName)
      channel.map(_.put(message1, Priority.Low))

      val properties = new AMQP.BasicProperties.Builder()
        .contentType("application/json").deliveryMode(DeliveryMode.Persistent)
        .priority(toRMqPriority(Priority.Low))
        .build()

      verify(rmqChannel).basicPublish(
        "",
        queueName,
        properties,
        jsonFormat.writes(message1).toString().getBytes
      )
    }

    "fail if cannot queue" in {
      val (rmqChannel, _, _, factory) = setup
      when(rmqChannel.basicPublish(
        meq(""),
        meq(queueName),
        any[AMQP.BasicProperties],
        meq(jsonFormat.writes(message1).toString().getBytes)
      )).thenThrow(new RuntimeException("Boom"))

      val channel = factory.outChannel(queueName)

      channel match {
        case Success(channel) =>
          intercept[QueueException] {
            channel.put(message1, Priority.Low)
          }
        case _ => fail("channel was failure")
      }
    }
  }

  "close" should {
    "Close the connection and the open rmqChannel" in {
      val (rmqChannel, connection, rabbitFactory, factory) = setup
      factory.outChannel(queueName).map(_.close())
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
