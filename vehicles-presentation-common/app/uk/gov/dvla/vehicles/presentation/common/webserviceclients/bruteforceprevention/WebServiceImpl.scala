package uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.ws.{Response, WS}
import scala.concurrent.Future

final class WebServiceImpl @Inject()(config: BruteForcePreventionConfig) extends BruteForcePreventionWebService {
  private val baseUrl: String = config.baseUrl
  private val requestTimeout: Int = config.requestTimeout
  private val serviceName: String = config.nameHeader
  private val maxRetries: String = config.maxAttemptsHeader.toString
  private val keyExpire: String = config.expiryHeader

  override def callBruteForce(vrm: String): Future[Response] = {
    val endPoint = s"$baseUrl/security"
    Logger.debug(s"Calling brute force prevention on $endPoint with vrm: $vrm")
    WS.url(endPoint).
      withHeaders("serviceName" -> serviceName).
      withHeaders("maxRetries" -> maxRetries).
      withHeaders("keyExpire" -> keyExpire).
      withRequestTimeout(requestTimeout). // Timeout is in milliseconds
      post(Map("tokenList" -> Seq(vrm)))
  }
}