package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Inject
import play.api.mvc.{Cookie, Cookies, Filter, Headers, RequestHeader, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

/**
  * This class is responsible for making sure that the two session cookies are present in the RequestHeader
  * The two session cookies are the tracking id and the session secret.
  *
  * If they are not present this filter will ensure they are created and added to the RequestHeader
  *
  * @param sessionFactory The session factory that is responsible for creating the session cookies if they
  *                       are missing
  */
class EnsureSessionCreatedFilter @Inject()(sessionFactory: ClientSideSessionFactory) extends Filter {

  /**
    * This method adds the session cookies to the RequestHeader that is passed through the filter chain
    *
    * @param requestHeader the HTTP request header
    * @param newSessionCookies new session cookies
    * @return A new RequestHeader whose cookie collection contains the session cookies
    */
  private def addSessionCookiesToRequestHeader(requestHeader: RequestHeader,
                                               newSessionCookies: Seq[Cookie]): RequestHeader = {
    val requestCookiesString = requestHeader.headers.get(play.api.http.HeaderNames.COOKIE).getOrElse("")
    val mergedCookiesString = Cookies.merge(requestCookiesString, newSessionCookies)

    val updatedRequestHeadersMap = requestHeader.headers.toMap +
      (play.api.http.HeaderNames.COOKIE -> Seq(mergedCookiesString))

    val headerWithSessionCookies = new Headers {
      val data = updatedRequestHeadersMap.toSeq
    }
    requestHeader.copy(headers = headerWithSessionCookies)
  }

  def apply(nextFilter: (RequestHeader) => Future[Result])
           (requestHeader: RequestHeader): Future[Result] =
    sessionFactory.newSessionCookiesIfNeeded(requestHeader.cookies) match {
      // A filled Option containing cookies indicates that the session cookies were missing from the request
      // the cookies in the Option now need to be added to the request
      case Some(newSessionCookies) =>
        val requestWithNewSessionCookies = addSessionCookiesToRequestHeader(requestHeader, newSessionCookies)

        nextFilter(requestWithNewSessionCookies).map { simpleResult =>
          simpleResult.withCookies(newSessionCookies: _*)
        }
      // None indicates the session cookies have been found so just call the next filter in the chain
      case None => nextFilter(requestHeader)
    }
}
