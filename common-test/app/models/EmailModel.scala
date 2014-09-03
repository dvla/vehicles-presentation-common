package models

import play.api.data.Forms._
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.email

case class EmailModel(email: String)

object EmailModel {

  object Form {
    final val EmailId = "Email"

    final val Mapping =  mapping(
      EmailId -> email
    )(EmailModel.apply)(EmailModel.unapply)

  }
}
