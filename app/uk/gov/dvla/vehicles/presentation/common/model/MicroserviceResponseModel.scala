package uk.gov.dvla.vehicles.presentation.common.model

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse

final case class MicroserviceResponseModel(msResponse: MicroserviceResponse)

object MicroserviceResponseModel {

  def content(response: MicroserviceResponse) = MicroserviceResponseModel(msResponse = response)

  final val MsResponseCacheKey = s"${CacheKeyPrefix}ms-response"

  implicit val JsonFormat = Json.format[MicroserviceResponseModel]
  implicit val Key = CacheKey[MicroserviceResponseModel](MsResponseCacheKey)
}