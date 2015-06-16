package uk.gov.dvla.vehicles.presentation.common.queue

import java.util.Date

import com.rabbitmq.client._
import org.mockito.Mockito.{verify, when, verifyNoMoreInteractions}
import org.mockito.Matchers.{any, eq => meq}
import play.api.libs.json.{Json, Format}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

import scala.concurrent.Future
import scala.util.Failure

class RabbitMqChannelFactorySpec extends UnitSpec {
  "outChannel" should {
    "successfully create a channel" in {
      val rMqChannel = mock[Channel]
      val connection = mock[Connection]
      val consumer = mock[DefaultConsumer]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenReturn(rMqChannel)
      when(consumer.getConsumerTag).thenReturn("")

      new RabbitMqChannelFactory(rabbitFactory).outChannel[String]("test-queue") should not be null

      verify(rabbitFactory).connection
      verify(connection).createChannel()
      verify(rMqChannel).queueDeclare("test-queue", true, false, false, null)
      verifyNoMoreInteractions(connection)
      verifyNoMoreInteractions(rMqChannel)
    }

    "return failure if we cannot get a connection" in {
      val connectionException = new RuntimeException("Boom2")
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenThrow(connectionException)
      new RabbitMqChannelFactory(rabbitFactory).outChannel[String]("test-queue") should be(
        Failure(connectionException)
      )
    }

    "close the resources if cannot create rabbitMqChannel" in {
      val channelException = new RuntimeException("Boom2")
      val connection = mock[Connection]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenThrow(channelException)

      new RabbitMqChannelFactory(rabbitFactory).outChannel[String]("test-queue") should be(
        Failure(channelException)
      )

      verify(connection).close()
      verify(connection).createChannel()
      verifyNoMoreInteractions(connection)
    }

    "close the resources if cannot declare a queue" in {
      val rMqChannel = mock[Channel]
      val queueCreateException = new RuntimeException("Boom2")
      val connection = mock[Connection]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenReturn(rMqChannel)
      when(rMqChannel.queueDeclare("test-queue", true, false, false, null)).thenThrow(queueCreateException)

      new RabbitMqChannelFactory(rabbitFactory).outChannel[String]("test-queue") should be(
        Failure(queueCreateException)
      )

      verify(connection).close()
      verify(connection).createChannel()
      verify(rMqChannel).queueDeclare("test-queue", true, false, false, null)
      verify(rMqChannel).close()
      verifyNoMoreInteractions(connection)
      verifyNoMoreInteractions(rMqChannel)
    }
  }

  "subscribe" should {
    "successfully create a channel" in {
      val rMqChannel = mock[Channel]
      val connection = mock[Connection]
      val consumer = mock[DefaultConsumer]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenReturn(rMqChannel)
      when(consumer.getConsumerTag).thenReturn("")

      new RabbitMqChannelFactory(rabbitFactory)
        .subscribe[String]("test-queue", s => Future.successful(MessageAck.Ack)) should not be null

      verify(rabbitFactory).connection
      verify(connection).createChannel()
      verify(rMqChannel).basicConsume(meq("test-queue"), meq(false), any[Consumer])
      verifyNoMoreInteractions(connection)
      verifyNoMoreInteractions(rMqChannel)
    }

    "return failure if we cannot get a connection" in {
      val connectionException = new RuntimeException("Boom2")
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenThrow(connectionException)
      new RabbitMqChannelFactory(rabbitFactory)
        .subscribe[String]("test-queue", s => Future.successful(MessageAck.Ack)) should be(
        Failure(connectionException)
      )
    }

    "close the resources if cannot create rabbitMqChannel" in {
      val channelException = new RuntimeException("Boom2")
      val connection = mock[Connection]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenThrow(channelException)

      new RabbitMqChannelFactory(rabbitFactory)
        .subscribe[String]("test-queue", s => Future.successful(MessageAck.Ack)) should be(
          Failure(channelException)
        )

      verify(connection).close()
      verify(connection).createChannel()
      verifyNoMoreInteractions(connection)
    }

    "close the resources if cannot declare a queue" in {
      val rMqChannel = mock[Channel]
      val consumerException = new RuntimeException("Boom2")
      val connection = mock[Connection]
      val rabbitFactory = mock[RabbitMqConnectionFactory]
      when(rabbitFactory.connection).thenReturn(connection)
      when(connection.createChannel()).thenReturn(rMqChannel)
      when(rMqChannel.basicConsume(meq("test-queue"), meq(false), any[Consumer])).thenThrow(consumerException)

      new RabbitMqChannelFactory(rabbitFactory)
        .subscribe[String]("test-queue", s => Future.successful(MessageAck.Ack)) should be(
        Failure(consumerException)
      )

      verify(connection).close()
      verify(connection).createChannel()
      verify(rMqChannel).basicConsume(meq("test-queue"), meq(false), any[Consumer])
      verify(rMqChannel).close()
      verifyNoMoreInteractions(connection)
      verifyNoMoreInteractions(rMqChannel)
    }
  }

  case class B(name: String, value: String)
  implicit val jsonFormat = Json.format[B]

  "e2e" should {
    import com.rabbitmq.client.Address

    "Enqueue/Dequeue messages" in {
      val config = TestRabbitMqConfig(
          rabbitMqVirtualHost = None,
          rabbitMqUserName = None,
          rabbitMqPassword = None,
          rabbitMqUseSsl = false,
          rabbitMqNetworkRecoverIntervalMs = None,
          rabbitMqHeartBeat = None,
          rabbitMqAddresses = Array(new Address("localhost", 5672))
      )

      val rMqConnectionFactory = new RabbitMqConnectionFactory(config, new ConnectionFactory)
      val factory = new RabbitMqChannelFactory(rMqConnectionFactory)
      val queueName = "test-queue-234324"
      val inChannel = factory.subscribe[B](queueName, b => Future.successful{
        println(s"${new Date()} Message received: " + b)
        MessageAck.Ack
      })
      val outChannel = factory.outChannel(queueName)

      outChannel.map { ch =>
        for (i <- 0 until 100000) ch.put(B(i.toString, "High priority"))
        for (i <- 100000 until 200000) ch.put(B(i.toString, "Low priority"), Priority.Low)
        for (i <- 200000 until 300000) ch.put(B(i.toString, "High priority"))
      } orElse {
      }

      Thread.sleep(20000)

      outChannel.map(_.close())
      inChannel.map(_.close())

    }
  }
}
