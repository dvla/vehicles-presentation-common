package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Postcode
import uk.gov.dvla.vehicles.presentation.common.views.models.AddressAndPostcodeViewModel

/**
 * UPRN is optional because if user is manually entering the address they will not be allowed to enter a UPRN, it is
 * only populated by address lookup services.
 */
final case class AddressModel(address: Seq[String]) {

  def formatPostcode: AddressModel = {
    val formattedPostcode = Postcode.formatPostcode(address.last)
    val addressUpdated = address.init :+ formattedPostcode // Get all except last element.
    this.copy(address = addressUpdated)
  }
}

object AddressModel {
  implicit val JsonFormat = Json.format[AddressModel]

  def from(address: AddressAndPostcodeViewModel): AddressModel =
    AddressModel(address = address.toViewFormat)

  def from(addressString: String): AddressModel =
    AddressModel(address = addressString.split(",") map (line => line.trim))
  
}
