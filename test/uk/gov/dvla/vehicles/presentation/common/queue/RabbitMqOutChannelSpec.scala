package uk.gov.dvla.vehicles.presentation.common.queue

import com.rabbitmq.client._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class RabbitMqOutChannelSpec extends UnitSpec {
  case class TB(name: String, value: Int, value1: Boolean)
  private implicit val jsonFormat = Json.format[TB]
  val message1 = TB("event-1-с български букви", 1, value1 = true)
  val message2 = TB("event-2  με ελληνικά", 2, value1 = true)
  val message3 = TB("event-3 希腊", 3, value1 = true)
  val queueName = "test-queue"

  "put" should {

    "Create a durable queue if it doesn't exist" in {
      val (rmqChannel, connection, rabbitFactory, factory) = setup
      val channel = factory.outChannel(queueName)
      channel.put(message1)

      verify(rmqChannel).basicPublish(
        "",
        queueName,
        MessageProperties.MINIMAL_PERSISTENT_BASIC,
        jsonFormat.writes(message1).toString().getBytes
      )
    }

    "send a persistent message" in {

    }

    "send messages with normal priority by default" in {

    }

    "send messages with explicit priority if specified" in {

    }

    "fail if cannot serialise the object" in {

    }

    "fail if cannot queue" in {

    }
  }

  "close" should {
    "Close the connection and the open rmqChannel" in {

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
