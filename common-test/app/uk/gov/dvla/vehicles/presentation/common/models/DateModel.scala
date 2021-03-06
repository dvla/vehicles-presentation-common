package uk.gov.dvla.vehicles.presentation.common.models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings
import uk.gov.dvla.vehicles.presentation.common.services.DateService

case class DateModel(date: LocalDate)

object DateModel {
  implicit val Key = CacheKey[DateModel]("test")
  implicit val JsonFormat = Json.format[DateModel]

  object Form {
    final val DateId = "DateFieldId1"

    final def Mapping(implicit dateService: DateService) = mapping(
      DateId -> mappings.Date.dateMapping.verifying(mappings.Date.notInTheFuture())
    )(DateModel.apply)(DateModel.unapply)
  }
}
