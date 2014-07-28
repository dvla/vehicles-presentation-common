package models.domain.common

import constraints.common.AddressLines._
import mappings.common.AddressLines._
import mappings.common.Uprn._
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.libs.json.Json
import mappings.common.AddressAndPostcode.AddressAndPostcodeCacheKey

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