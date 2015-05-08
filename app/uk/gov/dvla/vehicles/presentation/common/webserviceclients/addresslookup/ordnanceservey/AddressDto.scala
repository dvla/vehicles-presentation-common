package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import play.api.libs.json.Json

case class AddressDto(addressLine: String,
                      businessName: Option[String],
                      streetAddress1: String,
                      streetAddress2: Option[String],
                      streetAddress3: Option[String],
                      postTown: String,
                      postCode: String)

object AddressDto {
  implicit val JsonFormat = Json.format[AddressDto]
}