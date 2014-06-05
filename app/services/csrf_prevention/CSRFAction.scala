package services.csrf_prevention

import play.api.mvc._
import play.api.http.HeaderNames._
import services.csrf_prevention.CSRF._
import play.api.libs.iteratee._
import play.api.mvc.BodyParsers.parse._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.libs.Crypto

/**
 * An action that provides CSRF protection.
 *
 * @param tokenName The key used to store the token in the Play session.  Defaults to csrfToken.
 * @param cookieName If defined, causes the filter to store the token in a Cookie with this name instead of the session.
 * @param secureCookie If storing the token in a cookie, whether this Cookie should set the secure flag.  Defaults to
 *                     whether the session cookie is configured to be secure.
 * @param createIfNotFound Whether a new CSRF token should be created if it's not found.  Default creates one if it's
 *                         a GET request that accepts HTML.
 * @param tokenProvider A token provider to use.
 * @param next The composed action that is being protected.
 * @param errorHandler handling failed token error.
 */
class CSRFAction(next: EssentialAction,
                 tokenName: String = CSRFConf.TokenName,
                 createIfNotFound: RequestHeader => Boolean = CSRFConf.defaultCreateIfNotFound,
                 tokenProvider: TokenProvider = CSRFConf.defaultTokenProvider,
                 errorHandler: => ErrorHandler = CSRFConf.defaultErrorHandler) extends EssentialAction {

  import CSRFAction._

  // An iteratee that returns a forbidden result saying the CSRF check failed
  private def checkFailed(req: RequestHeader, msg: String): Iteratee[Array[Byte], SimpleResult] = Done(errorHandler.handle(req, msg))

  def apply(request: RequestHeader) = {

    // this function exists purely to aid readability
    def continue = next(request)

    // Only filter unsafe methods and content types
    if (CSRFConf.UnsafeMethods(request.method) && request.contentType.exists(CSRFConf.UnsafeContentTypes)) {

      if (checkCsrfBypass(request)) {
        continue
      } else {

        val headerToken = Crypto.signToken("qwerty")  // TODO lookup tracking-id session/cookie

        // Only proceed with checks if there is an incoming token in the header, otherwise there's no point
        request.contentType match {
              case Some("application/x-www-form-urlencoded") => checkFormBody(request, headerToken, tokenName, next)
              case Some("multipart/form-data") => checkMultipartBody(request, headerToken, tokenName, next)
              // No way to extract token from text plain body
              case Some("text/plain") => {
                filterLogger.trace("[CSRF] Check failed because text/plain request")
                checkFailed(request, "No CSRF token found for text/plain body")
              }
            }


      }
    } else if (getTokenFromHeader(request, tokenName).isEmpty && createIfNotFound(request)) {

      // No token in header and we have to create one if not found, so create a new token
      val newToken = tokenProvider.generateToken

      // The request
      val requestWithNewToken = request.copy(tags = request.tags + (Token.RequestTag -> newToken))

      // Once done, add it to the result
      next(requestWithNewToken).map(result =>
        CSRFAction.addTokenToResponse(tokenName, newToken, request, result))

    } else {
      filterLogger.trace("[CSRF] No check necessary")
      next(request)
    }
  }

  private def checkFormBody = checkBody[Map[String, Seq[String]]](tolerantFormUrlEncoded, identity) _

  private def checkMultipartBody = checkBody[MultipartFormData[Unit]](multipartFormData[Unit]({
    case _ => Iteratee.ignore[Array[Byte]].map(_ => MultipartFormData.FilePart("", "", None, ()))
  }), _.dataParts) _

  private def checkBody[T](parser: BodyParser[T], extractor: (T => Map[String, Seq[String]]))(request: RequestHeader, tokenFromHeader: String, tokenName: String, next: EssentialAction) = {
    // Take up to 100kb of the body
    val firstPartOfBody: Iteratee[Array[Byte], Array[Byte]] =
      Traversable.take[Array[Byte]](CSRFConf.PostBodyBuffer.asInstanceOf[Int]) &>> Iteratee.consume[Array[Byte]]()

    firstPartOfBody.flatMap {
      bytes: Array[Byte] =>
      // Parse the first 100kb
        val parsedBody = Enumerator(bytes) |>>> parser(request)

        Iteratee.flatten(parsedBody.map {
          parseResult =>
            val validToken = parseResult.fold(
              // error parsing the body, we couldn't find a valid token
              _ => false,
              // extract the token and verify
              body => (for {
                values <- extractor(body).get(tokenName)
                token <- values.headOption
              } yield tokenProvider.compareTokens(token, tokenFromHeader)).getOrElse(false)
            )

            if (validToken) {
              // Feed the buffered bytes into the next request, and return the iteratee
              filterLogger.trace("[CSRF] Valid token found in body")
              Iteratee.flatten(Enumerator(bytes) |>> next(request))
            } else {
              filterLogger.trace("[CSRF] Check failed because no or invalid token found in body")
              checkFailed(request, "Invalid token found in form body")
            }
        })
    }
  }

}

object CSRFAction {

  private[csrf_prevention] def getTokenFromHeader(request: RequestHeader, tokenName: String) = {
    request.session.get("csrfToken")
  }

  private[csrf_prevention] def checkCsrfBypass(request: RequestHeader) = {
    if (request.headers.get(CSRFConf.HeaderName).exists(_ == CSRFConf.HeaderNoCheck)) {

      // Since injecting arbitrary header values is not possible with a CSRF attack, the presence of this header
      // indicates that this is not a CSRF attack
      filterLogger.trace("[CSRF] Bypassing check because " + CSRFConf.HeaderName + ": " + CSRFConf.HeaderNoCheck + " header found")
      true

    } else if (request.headers.get("X-Requested-With").isDefined) {

      // AJAX requests are not CSRF attacks either because they are restricted to same origin policy
      filterLogger.trace("[CSRF] Bypassing check because X-Requested-With header found")
      true
    } else {
      false
    }
  }

  private[csrf_prevention] def addTokenToResponse(tokenName: String,
                                                  newToken: String, request: RequestHeader, result: SimpleResult) = {
    filterLogger.trace("[CSRF] Adding token to result: " + result)


    // Get the new session, or the incoming session
//    val session = request.session.data
//    val newSession = session + (tokenName -> newToken)
//    result.withSession(Session.deserialize(newSession))

    result.withSession(Session.deserialize(request.session.data))

  }
}

/**
 * CSRF check action.
 *
 * Apply this to all actions that require a CSRF check.
 */
object CSRFCheck {

  private class CSRFCheckAction[A](tokenName: String, tokenProvider: TokenProvider,
                                   errorHandler: ErrorHandler, wrapped: Action[A]) extends Action[A] {
    def parser = wrapped.parser

    def apply(request: Request[A]) = {

      // Maybe bypass
      if (CSRFAction.checkCsrfBypass(request) || !request.contentType.exists(CSRFConf.UnsafeContentTypes)) {
        wrapped(request)
      } else {
        // Get token from header
        CSRFAction.getTokenFromHeader(request, tokenName).flatMap {
          headerToken =>

            val form = request.body match {
              case body: play.api.mvc.AnyContent if body.asFormUrlEncoded.isDefined => body.asFormUrlEncoded.get
              case body: play.api.mvc.AnyContent if body.asMultipartFormData.isDefined => body.asMultipartFormData.get.asFormUrlEncoded
              case body: Map[_, _] => body.asInstanceOf[Map[String, Seq[String]]]
              case body: play.api.mvc.MultipartFormData[_] => body.asFormUrlEncoded
              case _ => Map.empty[String, Seq[String]]
            }
            form.get(tokenName).flatMap(_.headOption)

              // Execute if it matches
              .collect {
              case queryToken if tokenProvider.compareTokens(queryToken, headerToken) => wrapped(request)
            }
        }.getOrElse(Future.successful(errorHandler.handle(request, "CSRF token check failed")))
      }
    }
  }

  /**
   * Wrap an action in a CSRF check.
   */
  def apply[A](action: Action[A], errorHandler: ErrorHandler = CSRFConf.defaultErrorHandler): Action[A] =
    new CSRFCheckAction(CSRFConf.TokenName, CSRFConf.defaultTokenProvider, errorHandler, action)
}

/**
 * CSRF add token action.
 *
 * Apply this to all actions that render a form that contains a CSRF token.
 */
object CSRFAddToken {

  private class CSRFAddTokenAction[A](tokenName: String,
                                      tokenProvider: TokenProvider, wrapped: Action[A]) extends Action[A] {
    def parser = wrapped.parser

    def apply(request: Request[A]) = {
      if (CSRFAction.getTokenFromHeader(request, tokenName).isEmpty) {
        // No token in header and we have to create one if not found, so create a new token
        val newToken = tokenProvider.generateToken

        // The request
        val requestWithNewToken = new WrappedRequest(request) {
          override val tags = request.tags + (Token.RequestTag -> newToken)
        }

        // Once done, add it to the result
        wrapped(requestWithNewToken).map(result =>
          CSRFAction.addTokenToResponse(tokenName, newToken, request, result))
      } else {
        wrapped(request)
      }
    }
  }

  /**
   * Wrap an action in an action that ensures there is a CSRF token.
   */
  def apply[A](action: Action[A]): Action[A] =
    new CSRFAddTokenAction(CSRFConf.TokenName, CSRFConf.defaultTokenProvider, action)
}