package uk.gov.dvla.vehicles.presentation.common.filters

import com.google.inject.Inject
import play.api.mvc.{Cookies, Filter, Headers, RequestHeader, SimpleResult}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnsureSessionCreatedFilter @Inject()(sessionFactory: ClientSideSessionFactory) extends Filter {

  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] =
    sessionFactory.newSessionCookiesIfNeeded(requestHeader.cookies) match {
      case Some(cookies) =>
        val requestWithSessionCookies = {
          val requestCookiesString = requestHeader.headers.get(play.api.http.HeaderNames.COOKIE).getOrElse("")
          val mergedCookiesString = Cookies.merge(requestCookiesString, cookies)

          val updatedRequestHeadersMap = requestHeader.headers.toMap +
            (play.api.http.HeaderNames.COOKIE -> Seq(mergedCookiesString))

            val headerWithSessionCookies = new Headers {
            val data = updatedRequestHeadersMap.toSeq
          }
          requestHeader.copy(headers = headerWithSessionCookies)
        }

        nextFilter(requestWithSessionCookies).map { simpleResult =>
          simpleResult.withCookies(cookies: _*)
        }
      case None => nextFilter(requestHeader)
    }
}