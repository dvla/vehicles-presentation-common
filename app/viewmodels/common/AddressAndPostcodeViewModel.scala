package viewmodels.common

import constraints.common.AddressLines.validAddressLines
import mappings.common.Uprn.{UprnId, uprn}
import models.domain.common.CacheKey
import play.api.data.Forms.mapping
import play.api.data.Mapping
import play.api.libs.json.Json
import viewmodels.common.AddressLinesViewModel.Form.{AddressLinesId, mapping => addressLines}

case class AddressAndPostcodeViewModel(uprn: Option[Int] = None, addressLinesModel: AddressLinesViewModel) {
  def toViewFormat(postcode: String): Seq[String] = addressLinesModel.toViewFormat :+ postcode
}

object AddressAndPostcodeViewModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeViewModel]
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"
  implicit val Key = CacheKey[AddressAndPostcodeViewModel](AddressAndPostcodeCacheKey)

  object Form {
    final val Mapping: Mapping[AddressAndPostcodeViewModel] = mapping(
      UprnId -> uprn,
      AddressLinesId -> addressLines.verifying(validAddressLines)
    )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)
  }
}
