package helpers.common

import play.api.http.HeaderNames.SET_COOKIE
import play.api.mvc.{SimpleResult, Cookies, Cookie}

object CookieHelper {
  def fetchCookiesFromHeaders(result: SimpleResult): Seq[Cookie] =
    result.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
}