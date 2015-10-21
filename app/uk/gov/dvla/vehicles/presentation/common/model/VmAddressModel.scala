package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.views.models.{AddressLinesViewModel, AddressAndPostcodeViewModel}


object VmAddressModel {
  implicit val JsonFormat = Json.format[AddressModel]

  def from(address: AddressAndPostcodeViewModel): AddressModel =
    AddressModel(address = joinAddressesIfNeeded(address.toViewFormat))

  def from(addressString: String): AddressModel =
    AddressModel(uprn = None, address = joinAddressesIfNeeded(addressString.split(",") map (line => line.trim)))

  private def countAllowedLineCharacters(s: String) = s.count(_.isLetter)

  private def joinAddressesIfNeeded(addresses: Seq[String]): Seq[String] = addresses.toList match {
    case head :: second :: tail  if countAllowedLineCharacters(head) < AddressLinesViewModel.Form.BuildingNameOrNumberMinLength =>
      joinAddressesIfNeeded(s"$head $second" :: tail)
    case _ => addresses
  }

}