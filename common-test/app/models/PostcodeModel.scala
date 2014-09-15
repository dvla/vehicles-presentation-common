package models

import play.api.data.Mapping
import play.api.data.Forms.{mapping, nonEmptyText}
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Postcode.validPostcode

case class PostcodeModel(postcode:String)

object PostcodeModel {

  object Form {
    final val PostcodeId = "Postcode"
    final val MinLength = 5
    final val MaxLength = 8

    final val Mapping =  mapping(
      PostcodeId -> postcode()
    )(PostcodeModel.apply)(PostcodeModel.unapply)

    def postcode (minLength: Int = MinLength, maxLength: Int = MaxLength): Mapping[String] = {
      nonEmptyText(minLength, maxLength) verifying validPostcode
    }
  }
}
