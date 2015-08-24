package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import com.google.inject.Inject
import play.api.libs.ws.{WSResponse, WS}
import play.api.Play.current
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats

final class WebServiceImpl @Inject()(config: BruteForcePreventionConfig) extends BruteForcePreventionWebService {
  private val baseUrl: String = config.baseUrl
  private val requestTimeoutMillis: Int = config.requestTimeoutMillis
  private val serviceName: String = config.nameHeader
  private val maxRetries: String = config.maxAttemptsHeader.toString
  private val keyExpire: String = config.expiryHeader

  override def callBruteForce(vrm: String, trackingId: TrackingId): Future[WSResponse] = {
    val endPoint = s"$baseUrl/security"
    logMessage(trackingId, Debug, s"Calling brute force prevention on $endPoint with vrm: ${LogFormats.anonymize(vrm)}")
    WS.url(endPoint).
      withHeaders("serviceName" -> serviceName).
      withHeaders("maxRetries" -> maxRetries).
      withHeaders("keyExpire" -> keyExpire).
      withRequestTimeout(requestTimeoutMillis).
      post(Map("tokenList" -> Seq(vrm)))
  }

  override def reset(vrm: String, trackingId: TrackingId): Future[WSResponse] = {
    val endPoint = s"$baseUrl/security/delete"
    logMessage(trackingId, Debug, s"Resetting brute force prevention on $endPoint for vrm: ${LogFormats.anonymize(vrm)}")
    WS.url(endPoint).
      withHeaders("serviceName" -> serviceName).
      withRequestTimeout(requestTimeoutMillis).
      post(Map("tokenList" -> Seq(vrm)))
  }
}
