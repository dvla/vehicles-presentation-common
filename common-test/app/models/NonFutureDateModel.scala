package models

import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings

case class NonFutureDateModel(nonFutureDate: LocalDate)

object NonFutureDateModel {
  implicit val Key = CacheKey[NonFutureDateModel]("test")
  implicit val JsonFormat = Json.format[NonFutureDateModel]

  object Form {
    final val NonFutureDateId = "nonFutureDateFieldId"

    final val Mapping = mapping(
      NonFutureDateId -> mappings.NonFutureDate.mapping
    )(NonFutureDateModel.apply)(NonFutureDateModel.unapply)
  }
}
