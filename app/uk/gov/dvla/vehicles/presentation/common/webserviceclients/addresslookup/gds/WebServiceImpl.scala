package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.gds

import com.google.inject.Inject
import play.api.Logger
import play.api.i18n.Lang
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.GDSAddressLookupConfig
import scala.concurrent.Future

final class WebServiceImpl @Inject()(config: GDSAddressLookupConfig) extends AddressLookupWebService with DVLALogger {
  private val baseUrl: String = config.baseUrl
  private val authorisation: String = config.authorisation
  private val requestTimeout: Int = config.requestTimeout

  def postcodeWithNoSpaces(postcode: String): String = postcode.filter(_ != ' ')

  // request should look like    (GET, "/addresses?postcode=kt70ej").withHeaders(validAuthHeader)
  override def callPostcodeWebService(postcode: String, trackingId: TrackingId, showBusinessName: Option[Boolean])
                                     (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/addresses?postcode=${ postcodeWithNoSpaces(postcode) }"
    logMessage(trackingId, Debug, s"Calling GDS postcode lookup service on $endPoint")
    WS.url(endPoint).
      withHeaders("AUTHORIZATION" -> authorisation).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      get()
  }

  override def callUprnWebService(uprn: String, trackingId: TrackingId)
                                 (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/uprn?uprn=$uprn"
    logMessage(trackingId, Debug, s"Calling GDS uprn lookup service on")
    WS.url(endPoint).
      withHeaders("AUTHORIZATION" -> authorisation).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      get()
  }

  override def callAddresses(postcode: String, trackingId: TrackingId)(implicit lang: Lang): Future[WSResponse] = ???
}
