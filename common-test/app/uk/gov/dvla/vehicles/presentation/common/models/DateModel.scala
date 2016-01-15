package uk.gov.dvla.vehicles.presentation.common.models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings
import uk.gov.dvla.vehicles.presentation.common.services.DateService

case class DateModel(optionalDate: Option[LocalDate], date: LocalDate)

object DateModel {
  implicit val Key = CacheKey[DateModel]("test")
  implicit val JsonFormat = Json.format[DateModel]

  object Form {
    final val OptionalDateId = "DateFieldId"
    final val DateId = "DateFieldId1"

    final def Mapping(implicit dateService: DateService) = mapping(
      OptionalDateId -> mappings.Date.optionalNonFutureDateMapping,
      DateId -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture())
    )(DateModel.apply)(DateModel.unapply)
  }
}
