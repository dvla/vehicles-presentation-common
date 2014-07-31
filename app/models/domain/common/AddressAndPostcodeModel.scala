package models.domain.common

import constraints.common.AddressLines.validAddressLines
import mappings.common.AddressLines.addressLines
import mappings.common.Uprn.{uprn, UprnId}
import play.api.data.Forms.mapping
import play.api.data.Mapping
import play.api.libs.json.Json
import mappings.common.AddressAndPostcode.AddressAndPostcodeCacheKey
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import widgets.mappings.AddressLines.AddressLinesId

case class AddressAndPostcodeModel(uprn: Option[Int] = None, addressLinesModel: AddressLinesModel) {
  def toViewFormat(postcode: String): Seq[String] = addressLinesModel.toViewFormat :+ postcode
}

object AddressAndPostcodeModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeModel]
  implicit val Key = CacheKey[AddressAndPostcodeModel](AddressAndPostcodeCacheKey)

  final val FormMapping: Mapping[AddressAndPostcodeModel] = mapping(
    UprnId -> uprn,
    AddressLinesId -> addressLines.verifying(validAddressLines)
  )(AddressAndPostcodeModel.apply)(AddressAndPostcodeModel.unapply)
}