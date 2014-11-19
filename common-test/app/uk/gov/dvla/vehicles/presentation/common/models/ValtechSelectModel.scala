package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Mapping
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import play.api.data.validation.{ValidationError, Invalid, Valid, Constraint}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

case class ValtechSelectModel(selectedInList: String)

object ValtechSelectModel {

  implicit val JsonFormat = Json.format[ValtechSelectModel]
  final val ValtechSelectModelCacheKey = "valtechSelectModel"
  implicit val Key = CacheKey[ValtechSelectModel](value = ValtechSelectModelCacheKey)

  object Form {
    final val SelectId = "demo_select"
    final val FirstOption = "Option 1"
    final val SecondOption = "Option 2"

    final val DropDownOptions = Map(
      FirstOption -> "This is the first option",
      SecondOption -> "This is the second option"
    )

    final val Mapping = mapping(
        SelectId -> dropDown(DropDownOptions)
      )(ValtechSelectModel.apply)(ValtechSelectModel.unapply)

    def dropDown(dropDownOptions: Map[String, String]): Mapping[String] = {
      nonEmptyText(maxLength = 12) verifying validDropDown(dropDownOptions)
    }

    def validDropDown(dropDownOptions: Map[String, String]): Constraint[String] = Constraint[String]("constraint.validDropDown") { input =>
      dropDownOptions.contains(input) match {
        case true => Valid
        case false => Invalid(ValidationError("error.dropDownInvalid"))
      }
    }
  }
}
