package uk.gov.dvla.vehicles.presentation.common.models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings
import uk.gov.dvla.vehicles.presentation.common.services.DateService

case class DateOfSaleModel(optionalDate: Option[LocalDate], date: LocalDate)

object DateOfSaleModel {
  implicit val Key = CacheKey[DateOfSaleModel]("test")
  implicit val JsonFormat = Json.format[DateOfSaleModel]

  object Form {
    final val OptionalDateId = "DateOfSaleFieldId"
    final val DateId = "DateOfSaleFieldId1"

    final def detailMapping(implicit dateService: DateService) = mapping(
      OptionalDateId -> mappings.Date.optionalNonFutureDateMapping,
      DateId -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture())
    )(DateOfSaleModel.apply)(DateOfSaleModel.unapply)
  }
}
