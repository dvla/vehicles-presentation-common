package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Inject
import org.apache.commons.codec.binary.Base64
import play.api.http.ContentTypes.HTML
import play.api.http.HeaderNames.REFERER
import play.api.http.HttpVerbs.{GET, POST}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.Crypto
import play.api.libs.iteratee.{Done, Enumerator, Iteratee, Traversable}
import play.api.Logger
import play.api.mvc.BodyParsers.parse.tolerantFormUrlEncoded
import play.api.mvc.{EssentialAction, EssentialFilter, Headers, RequestHeader, Result, Results}
import scala.util.Try
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{TrackingId, AesEncryption, ClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.RichCookies
import common.ConfigProperties.{getProperty, getOptionalProperty, stringProp, booleanProp}
import common.LogFormats.DVLALogger

class CsrfPreventionFilter @Inject()
(implicit clientSideSessionFactory: ClientSideSessionFactory) extends EssentialFilter {

  def apply(next: EssentialAction): EssentialAction = new CsrfPreventionAction(next)
}

//final case class CsrfPreventionException(nestedException: Throwable) extends Exception(nestedException: Throwable)

/**
 * This class is based upon the Play's v2.2 CSRF protection. It has been stripped of code not relevant to this project, and
 * project specific exception handling and aesEncryption has been added. The unmarshalling and onward streaming in the
 * checkBody method is as Play intended it apart from the token comparison.
 *
 * https://www.playframework.com/documentation/2.2.x/ScalaCsrf
 *
 */
class CsrfPreventionAction(next: EssentialAction)
                          (implicit clientSideSessionFactory: ClientSideSessionFactory)
                          extends EssentialAction with DVLALogger {

  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.aesEncryption
  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.buildTokenWithUri
  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.buildTokenWithReferer
  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.preventionEnabled
  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.split
  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName

  def apply(requestHeader: RequestHeader) = {
    // check if csrf prevention is switched on
    if (preventionEnabled) {
      if (requestHeader.method == POST) {
        // TODO remove debris around reading the whitelist from config.
        if (requestHeader.contentType.exists(_ == "application/x-www-form-urlencoded" )) checkBody(requestHeader, next)
        else error("POST contentType was not urlencoded")(requestHeader)
      } else if (requestHeader.method == GET && requestHeader.accepts(HTML)) {
        next(requestWithNewToken(requestHeader))
      } else next(requestHeader)
    } else next(requestHeader)
  }

  private def requestWithNewToken(requestHeader: RequestHeader) = {
    // No token in header and we have to create one if not found, so create a new token
    val newToken = buildTokenWithUri(requestHeader.cookies.trackingId(), requestHeader.uri)
    val newEncryptedToken = aesEncryption.encrypt(newToken)
    val newSignedEncryptedToken = Crypto.signToken(newEncryptedToken)
    requestHeader.copy(tags = requestHeader.tags + (TokenName -> newSignedEncryptedToken))
  }

  private def checkBody(requestHeader: RequestHeader, next: EssentialAction) = {
    val firstPartOfBody: Iteratee[Array[Byte], Array[Byte]] =
      Traversable.take[Array[Byte]](102400L.asInstanceOf[Int]) &>> Iteratee.consume[Array[Byte]]()
    firstPartOfBody.flatMap { bytes: Array[Byte] =>
      val parsedBody = Enumerator(bytes) |>>> tolerantFormUrlEncoded(requestHeader)
      Iteratee.flatten(parsedBody.map { parseResult =>
        if (isValidTokenInPostBody(parseResult, requestHeader) || isValidTokenInPostUrl(requestHeader))
          Iteratee.flatten(Enumerator(bytes) |>> next(requestHeader))
        else
          error("No valid token found in form body or cookies")(requestHeader)
      })
    }
  }

  private def isValidTokenInPostBody(parseResult: Either[Result, Map[String, Seq[String]]],
                                     requestHeader: RequestHeader) =
    parseResult.fold(
      simpleResult => false, // valid token not found
      body => (for {// valid token found
        values <- identity(body).get(TokenName)
        tokenOpt <- values.headOption
        token <- Crypto.extractSignedToken(tokenOpt)
      } yield {
        val decryptedExtractedSignedToken = aesEncryption.decrypt(token)
        val splitDecryptedExtractedSignedToken = split(decryptedExtractedSignedToken)
        val headerToken = buildTokenWithReferer(
          requestHeader.cookies.trackingId(),
          requestHeader.headers
        )
        //TODO name the tuple parts accordingly instead of referencing it by number
        val splitTokenFromHeader = split(headerToken)
        (splitDecryptedExtractedSignedToken._1 == splitTokenFromHeader._1) &&
          splitTokenFromHeader._2.contains(splitDecryptedExtractedSignedToken._2)
      }).getOrElse(false)
    )

  private def isValidTokenInPostUrl(requestHeader: RequestHeader): Boolean = {
    val result = {
      val tokenEncryptedAndUriEncoded = requestHeader.path.split("/").last // Split the path based on "/" character, if there is a token it will be at the end
      val tokenEncrypted = new String(Base64.decodeBase64(tokenEncryptedAndUriEncoded))

      Crypto.extractSignedToken(tokenEncrypted)
        .map( signedToken => aesEncryption.decrypt(signedToken))
        .map(decryptedExtractedSignedToken => split(decryptedExtractedSignedToken))
    }

    val trackingIdFromCookie = requestHeader.cookies.trackingId()
    val refererFromCookie = requestHeader.cookies.getString(REFERER)

    result.exists{
      case (trackingIdFromUrl, refererFromUrl) =>
        TrackingId(trackingIdFromUrl) == trackingIdFromCookie && refererFromCookie.exists(_.contains(refererFromUrl))
    }
  }

  private def error(message: String)(requestHeader: RequestHeader): Iteratee[Array[Byte], Result] = {
    val remoteAddress = requestHeader.remoteAddress
    val path = requestHeader.path
    val msg = s"CsrfPreventionException remote address: $remoteAddress path: $path, message: $message"
    logMessage(requestHeader.cookies.trackingId(), Error, msg)

    Done(Results.Forbidden)
  }
}

object CsrfPreventionAction {

  final val TokenName = "csrf_prevention_token"
  private final val Delimiter = "-"
  lazy val preventionEnabled = {
    val enabled = getOptionalProperty[Boolean]("csrf.prevention").getOrElse(true)
    Logger.info(s"[CSRF] is ${if (enabled) "enabled" else "disabled"}")
    enabled
  }
  lazy val postWhitelist = getProperty[String]("csrf.post.whitelist").split(",")
/*
  private val aesEncryption = {
    if (getOptionalProperty[Boolean]("encryptCookies").getOrElse(true)) {
      Logger.info("[CSRF] is using AES encryption for the prevention token")
      new AesEncryption()
    } else {
      Logger.info("[CSRF] is using no encryption for the prevention token")
      new NoEncryption()
    }
  }
*/
  private val aesEncryption = {
    Logger.info("[CSRF] is using AES encryption for the prevention token")
    new AesEncryption()
  }

  case class CsrfPreventionToken(value: String)

  // TODO : Trap the missing token exception differently?
  implicit def getToken(implicit request: RequestHeader,
                        clientSideSessionFactory: ClientSideSessionFactory): CsrfPreventionToken =
    Try {
      CsrfPreventionToken(
        Crypto.signToken(
          aesEncryption.encrypt(
            buildTokenWithUri(request.cookies.trackingId(), request.uri)
          )
        )
      )
    }.getOrElse(CsrfPreventionToken(""))

  private def buildTokenWithReferer(trackingId: TrackingId, requestHeaders: Headers) = {
    trackingId.value + Delimiter + requestHeaders.get(REFERER).getOrElse("INVALID")
  }

  private def buildTokenWithUri(trackingId: TrackingId, uri: String) = {
    trackingId.value + Delimiter + uri
  }

  private def split(token: String): (String, String) = {
    (token.split(Delimiter)(0), token.drop(token.indexOf(Delimiter) + 1))
  }
}
