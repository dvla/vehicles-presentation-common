package uk.gov.dvla.vehicles.presentation.common.queue

case class TestRabbitMqConfig(rabbitMqVirtualHost: Option[String],
                              rabbitMqUserName: Option[String],
                              rabbitMqPassword: Option[String],
                              rabbitMqUseSsl: Boolean,
                              rabbitMqNetworkRecoverIntervalMs: Option[Long],
                              rabbitMqHeartBeat: Option[Int],
                              rabbitMqAddresses: Array[com.rabbitmq.client.Address]) extends RabbitMqConfig
