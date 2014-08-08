package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.views.models.AddressAndPostcodeViewModel

/**
 * UPRN is optional because if user is manually entering the address they will not be allowed to enter a UPRN, it is
 * only populated by address lookup services.
 */
final case class AddressModel(uprn: Option[Long] = None, address: Seq[String])

object AddressModel {
  implicit val JsonFormat = Json.format[AddressModel]

  def from(address: AddressAndPostcodeViewModel, postcode: String): AddressModel =
    AddressModel(address = address.toViewFormat(postcode))
}
