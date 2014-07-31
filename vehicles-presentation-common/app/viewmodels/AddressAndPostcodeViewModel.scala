package viewmodels

import constraints.AddressLinesConstraints
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import viewmodels.AddressLinesViewModel.Form.{AddressLinesId, mapping => addressLines}

case class AddressAndPostcodeViewModel(uprn: Option[Int] = None, addressLinesModel: AddressLinesViewModel) {
  def toViewFormat(postcode: String): Seq[String] = addressLinesModel.toViewFormat :+ postcode
}

object AddressAndPostcodeViewModel {
  implicit val AddressAndPostcodeModelFormat = Json.format[AddressAndPostcodeViewModel]
  final val AddressAndPostcodeCacheKey = "addressAndPostcodeCacheKey"
  implicit val Key = CacheKey[AddressAndPostcodeViewModel](AddressAndPostcodeCacheKey)

  object Form {
    final val UprnId = "uprn"

    final val Mapping: Mapping[AddressAndPostcodeViewModel] = mapping(
      UprnId -> uprn,
      AddressLinesId -> addressLines.verifying(AddressLinesConstraints.validAddressLines)
    )(AddressAndPostcodeViewModel.apply)(AddressAndPostcodeViewModel.unapply)

    private def uprn: Mapping[Option[Int]] = optional(number)
  }
}
