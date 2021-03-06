package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import com.google.inject.Inject
import play.api.i18n.Lang
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService

final class WebServiceImpl @Inject()(config: OrdnanceSurveyConfig) extends AddressLookupWebService with DVLALogger {
  private val baseUrl: String = config.baseUrl
  private val requestTimeout: Int = config.requestTimeout

  def callAddresses(postcode: String, trackingId: TrackingId)
                   (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/addresses?" +
      postcodeParam(postcode) +
      languageParam

    val postcodeToLog = LogFormats.anonymize(postcode)
    logMessage(trackingId, Debug, s"Calling ordnance-survey addresses lookup micro-service with $postcodeToLog")
    WS.url(endPoint)
      .withHeaders(HttpHeaders.TrackingId -> trackingId.value)
      .withRequestTimeout(requestTimeout) // Timeout is in milliseconds
      .get()
  }

  def postcodeWithNoSpaces(postcode: String): String = postcode.filter(_ != ' ')

  private def postcodeParam(postcode: String) = s"postcode=${postcodeWithNoSpaces(postcode)}"

  private def languageParam(implicit lang: Lang) = s"&languageCode=${lang.code.toUpperCase}"

}