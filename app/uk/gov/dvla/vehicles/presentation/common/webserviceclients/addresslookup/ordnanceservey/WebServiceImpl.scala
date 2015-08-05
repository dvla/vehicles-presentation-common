package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey

import com.google.inject.Inject
import play.api.Logger
import play.api.i18n.Lang
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.HttpHeaders
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupWebService

final class WebServiceImpl @Inject()(config: OrdnanceSurveyConfig) extends AddressLookupWebService with DVLALogger {
  private val baseUrl: String = config.baseUrl
  private val requestTimeout: Int = config.requestTimeout

  override def callPostcodeWebService(postcode: String, trackingId: TrackingId, showBusinessName: Option[Boolean])
                                     (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/postcode-to-address?" +
      postcodeParam(postcode) +
      languageParam +
      showBusinessNameParam(showBusinessName) +
      trackingIdParam(trackingId)

    val postcodeToLog = LogFormats.anonymize(postcode)
    logMessage(trackingId, Debug,s"Calling ordnance-survey postcode lookup micro-service " +
      s"with $postcodeToLog") // $endPoint...")
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      get()
  }

  def callAddresses(postcode: String, trackingId: TrackingId)
                   (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/addresses?" +
      postcodeParam(postcode) +
      languageParam +
      trackingIdParam(trackingId)

    val postcodeToLog = LogFormats.anonymize(postcode)

    logMessage(trackingId, Debug, s"Calling ordnance-survey addresses lookup micro-service " +
      s"with $postcodeToLog") // $endPoint...")
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      get()
  }

  override def callUprnWebService(uprn: String, trackingId: TrackingId)
                                 (implicit lang: Lang): Future[WSResponse] = {
    val endPoint = s"$baseUrl/uprn-to-address?" +
      s"uprn=$uprn" +
      languageParam +
      trackingIdParam(trackingId)

    val uprnToLog = LogFormats.anonymize(uprn)
    logMessage(trackingId, Debug,s"Calling ordnance-survey uprn lookup micro-service with $uprnToLog")
    WS.url(endPoint).
      withHeaders(HttpHeaders.TrackingId -> trackingId.value).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      get()
  }

  def postcodeWithNoSpaces(postcode: String): String = postcode.filter(_ != ' ')

  private def postcodeParam(postcode: String) = s"postcode=${postcodeWithNoSpaces(postcode)}"

  private def trackingIdParam(trackingId: TrackingId): String =
    s"&${ClientSideSessionFactory.TrackingIdCookieName}=$trackingId"

  private def languageParam(implicit lang: Lang) = s"&languageCode=${lang.code.toUpperCase}"

  private def showBusinessNameParam(showBusinessName: Option[Boolean]) =
    if(showBusinessName.isDefined) s"&showBusinessName=${showBusinessName.get}" else ""
}