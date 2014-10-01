package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Inject
import play.api.http.ContentTypes.HTML
import play.api.http.HeaderNames.REFERER
import play.api.http.HttpVerbs.{GET, POST}
import play.api.libs.Crypto
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Enumerator, Iteratee, Traversable}
import play.api.mvc.BodyParsers.parse.tolerantFormUrlEncoded
import play.api.mvc._
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{AesEncryption, ClientSideSessionFactory}
import scala.util.Try

class CsrfPreventionFilter @Inject()
(implicit clientSideSessionFactory: ClientSideSessionFactory) extends EssentialFilter {

  def apply(next: EssentialAction): EssentialAction = new CsrfPreventionAction(next)
}

final case class CsrfPreventionException(nestedException: Throwable) extends Exception(nestedException: Throwable)

/**
 * This class is based upon the Play's v2.2 CSRF protection. It has been stripped of code not relevant to this project, and
 * project specific exception handling and aesEncryption has been added. The unmarshalling and onward streaming in the
 * checkBody method is as Play intended it apart from the token comparison.
 *
 * https://www.playframework.com/documentation/2.2.x/ScalaCsrf
 *
 */
class CsrfPreventionAction(next: EssentialAction)
                          (implicit clientSideSessionFactory: ClientSideSessionFactory) extends EssentialAction {

  import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction._

  def apply(requestHeader: RequestHeader) = {
    // check if csrf prevention is switched on
    if (preventionEnabled) {
      if (requestHeader.method == POST) {
        // TODO remove debris around reading the whitelist from config.
        if (requestHeader.contentType.get == "application/x-www-form-urlencoded") checkBody(requestHeader, next)
        else throw new CsrfPreventionException(new Throwable("POST contentType was not urlencoded"))
      } else if (requestHeader.method == GET && requestHeader.accepts(HTML)) {
        next(requestWithNewToken(requestHeader))
      } else next(requestHeader)
    } else next(requestHeader)
  }

  private def requestWithNewToken(requestHeader: RequestHeader) = {
    // No token in header and we have to create one if not found, so create a new token
    val newToken = buildTokenWithUri(requestHeader.cookies.trackingId, requestHeader.uri)
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
          throw new CsrfPreventionException(new Throwable("No valid token found in form body or cookies"))
      })
    }
  }

  private def isValidTokenInPostBody(parseResult: Either[Result, Map[String, Seq[String]]],
                                     requestHeader: RequestHeader) =
    parseResult.fold(
      simpleResult => false, // valid token not found
      body => (for {// valid token found
        values <- identity(body).get(TokenName)
        token <- values.headOption
      } yield {
        val decryptedExtractedSignedToken = aesEncryption.decrypt(Crypto.extractSignedToken(token).getOrElse(
          throw new CsrfPreventionException(new Throwable("Invalid or no token found in form body"))))
        val splitDecryptedExtractedSignedToken = split(decryptedExtractedSignedToken)
        val headerToken = buildTokenWithReferer(
          requestHeader.cookies.trackingId,
          requestHeader.headers
        )
        //TODO name the tuple parts accordingly instead of referencing it by number
        val splitTokenFromHeader = split(headerToken)
        (splitDecryptedExtractedSignedToken._1 == splitTokenFromHeader._1) &&
          splitTokenFromHeader._2.contains(splitDecryptedExtractedSignedToken._2)
      }).getOrElse(false)
    )

  private def isValidTokenInPostUrl(requestHeader: RequestHeader) = {
    val (token, uri) = {
      val tokenEncryptedAndUriEncoded = requestHeader.path.split("/").last // Split the path based on "/" character, if there is a token it will be at the end
      val tokenEncrypted = play.utils.UriEncoding.decodePathSegment(tokenEncryptedAndUriEncoded, "UTF-8")
      val signedToken = Crypto.extractSignedToken(tokenEncrypted).getOrElse(
        throw new CsrfPreventionException(new Throwable("Invalid or no token found in POST url")))
      val decryptedExtractedSignedToken = aesEncryption.decrypt(signedToken)
      split(decryptedExtractedSignedToken)
    }

    val (trackingId, refererUri) = {
      val trackingId = requestHeader.cookies.trackingId
      val referer = {
        val refererOpt = requestHeader.cookies.getString(REFERER)
        refererOpt match {
          case Some(value) => value
          case None => throw new CsrfPreventionException(new Throwable("No REFERER found in cookies")) // Fetch from cookie if it exists.
        }
      }
      val headerToken = buildTokenWithUri(trackingId = trackingId, uri = referer)
      split(headerToken)
    }

    (token == trackingId) && refererUri.contains(uri)
  }
}

object CsrfPreventionAction {

  final val TokenName = "csrf_prevention_token"
  private final val Delimiter = "-"
  lazy val preventionEnabled = getProperty("csrf.prevention", default = true)
  lazy val postWhitelist = getProperty("csrf.post.whitelist", "").split(",")
  private val aesEncryption = new AesEncryption()

  case class CsrfPreventionToken(value: String)

  // TODO : Trap the missing token exception differently?
  implicit def getToken(implicit request: RequestHeader,
                        clientSideSessionFactory: ClientSideSessionFactory): CsrfPreventionToken =
    Try {
      CsrfPreventionToken(
        Crypto.signToken(
          aesEncryption.encrypt(
            buildTokenWithUri(request.cookies.trackingId, request.uri)
          )
        )
      )
    }.getOrElse(throw new CsrfPreventionException(new Throwable("No CSRF token found")))

  private def buildTokenWithReferer(trackingId: String, requestHeaders: Headers) = {
    trackingId + Delimiter + requestHeaders.get(REFERER).getOrElse("INVALID")
  }

  private def buildTokenWithUri(trackingId: String, uri: String) = {
    trackingId + Delimiter + uri
  }

  private def split(token: String): (String, String) = {
    (token.split(Delimiter)(0), token.drop(token.indexOf(Delimiter) + 1))
  }
}
