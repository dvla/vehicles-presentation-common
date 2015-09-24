package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms._
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.emailConfirm
import play.api.data.Forms.nonEmptyText

case class ValidateHtml5ClientSideModel(email: String,
                                        MileageId: String,
                                        DocumentReferenceNumberId: String)

object ValidateHtml5ClientSideModel {
  implicit val Key = CacheKey[ValidateHtml5ClientSideModel]("test-html5-model")
  implicit val JsonFormat = Json.format[ValidateHtml5ClientSideModel]

  object Form {
    final val EmailId = "Email"
    final val MileageId = "mileage"
    final val DocumentReferenceNumberId = "DocumentReferenceNumber"

    final val Mapping =  mapping(
      EmailId -> emailConfirm,
      MileageId -> nonEmptyText(),
      DocumentReferenceNumberId -> nonEmptyText()


    )(ValidateHtml5ClientSideModel.apply)(ValidateHtml5ClientSideModel.unapply)
  }
}
