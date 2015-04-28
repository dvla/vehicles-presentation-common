package uk.gov.dvla.vehicles.presentation.common.model

case class Address(streetAddress1: String,
                   streetAddress2: Option[String],
                   streetAddress3: Option[String],
                   postTown: String,
                   postCode: String,
                   remember: Boolean)

