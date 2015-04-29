package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms._
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.emailConfirm

case class EmailModel(email: Option[String])

object EmailModel {
  implicit val Key = CacheKey[EmailModel]("test-email-model")
  implicit val JsonFormat = Json.format[EmailModel]

  object Form {
    final val EmailId = "Email"

    final val Mapping =  mapping(
      EmailId -> optional(emailConfirm)
    )(EmailModel.apply)(EmailModel.unapply)
  }
}
