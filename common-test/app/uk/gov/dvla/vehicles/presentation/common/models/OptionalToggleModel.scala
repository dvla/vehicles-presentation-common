package uk.gov.dvla.vehicles.presentation.common.models

import org.joda.time.LocalDate
import play.api.data.Forms._
import uk.gov.dvla.vehicles.presentation.common.mappings.Date
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.optional


case class OptionalToggleModel(text: Option[String], num: Option[Int], date: Option[LocalDate])

object OptionalToggleModel {

  object Form {
    final val OptionalStringOptionId = "optional-string-option"
    final val OptionalStringId = "optional-string"
    final val OptionalIntOptionId = "optional-int-option"
    final val OptionalIntId = "optional-int"
    final val OptionalDateOptionId = "optional-date-option"
    final val OptionalDateId = "optional-date"

    final val Mapping = mapping(
      OptionalStringOptionId -> optional(nonEmptyText(0, 10).withPrefix(OptionalStringId)),
      OptionalIntOptionId -> optional(number(0, 10).withPrefix(OptionalIntId)),
      OptionalDateOptionId -> optional(Date.dateMapping.withPrefix(OptionalDateId ))
    )(OptionalToggleModel.apply)(OptionalToggleModel.unapply)
  }
}
