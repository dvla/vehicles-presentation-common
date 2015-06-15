package uk.gov.dvla.vehicles.presentation.common.queue

import com.rabbitmq.client.ConnectionFactory
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import org.mockito.Mockito.{when, verify, never, verifyNoMoreInteractions}
import com.rabbitmq.client.Address
import com.rabbitmq.client.Connection

class RabbitMqConnectionFactorySpec extends UnitSpec {
  "connection" should {
    "set all the config parameters with ssl" in {
      val config = mock[RabbitMqConfig]
      when(config.rabbitMqVirtualHost).thenReturn(Some("some-rmq-virtual-host"))
      when(config.rabbitMqUserName).thenReturn(Some("some-rmq-user"))
      when(config.rabbitMqPassword).thenReturn(Some("some-rmq-password"))
      when(config.rabbitMqUseSsl).thenReturn(true)
      when(config.rabbitMqNetworkRecoverIntervalMs).thenReturn(Some(456L))
      when(config.rabbitMqHearthbeat).thenReturn(Some(44))
      when(config.rabbitMqAddresses).thenReturn(Array(
        new Address("host1", 32), new Address("host2", 5534), new Address("host3", 44444))
      )
      val connection = mock[Connection]
      val factory = mock[ConnectionFactory]
      when(factory.newConnection(config.rabbitMqAddresses)).thenReturn(connection)

      new RabbitMqConnectionFactory(config, factory).connection should equal(connection)
      verify(factory).setVirtualHost(config.rabbitMqVirtualHost.get)
      verify(factory).setUsername(config.rabbitMqUserName.get)
      verify(factory).setPassword(config.rabbitMqPassword.get)
      verify(factory).useSslProtocol()
      verify(factory).setAutomaticRecoveryEnabled(true)
      verify(factory).setNetworkRecoveryInterval(config.rabbitMqNetworkRecoverIntervalMs.get)
      verify(factory).setRequestedHeartbeat(config.rabbitMqHearthbeat.get)
      verify(factory).newConnection(config.rabbitMqAddresses)
      verifyNoMoreInteractions(factory)
    }

    "not set the optional config parameters" in {
      val config = mock[RabbitMqConfig]
      when(config.rabbitMqVirtualHost).thenReturn(None)
      when(config.rabbitMqUserName).thenReturn(None)
      when(config.rabbitMqPassword).thenReturn(None)
      when(config.rabbitMqUseSsl).thenReturn(false)
      when(config.rabbitMqNetworkRecoverIntervalMs).thenReturn(None)
      when(config.rabbitMqHearthbeat).thenReturn(None)
      when(config.rabbitMqAddresses).thenReturn(Array(
        new Address("host1", 32), new Address("host2", 5534), new Address("host3", 44444))
      )
      val connection = mock[Connection]
      val factory = mock[ConnectionFactory]
      when(factory.newConnection(config.rabbitMqAddresses)).thenReturn(connection)

      new RabbitMqConnectionFactory(config, factory).connection should equal(connection)
      verify(factory).setAutomaticRecoveryEnabled(false)
      verify(factory).newConnection(config.rabbitMqAddresses)
      verifyNoMoreInteractions(factory)
    }
  }
}
