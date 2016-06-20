package uk.gov.dvla.vehicles.presentation.common.models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings
import uk.gov.dvla.vehicles.presentation.common.services.DateService

case class OptionalDateModel(optionalDate: Option[LocalDate])

object OptionalDateModel {
  implicit val Key = CacheKey[DateModel]("test")
  implicit val JsonFormat = Json.format[DateModel]

  object Form {
    final val OptionalDateId = "DateFieldId"

    final def Mapping(implicit dateService: DateService) = mapping(
      OptionalDateId -> mappings.OptionalDate.optionalNonFutureDateMapping
    )(OptionalDateModel.apply)(OptionalDateModel.unapply)
  }
}
