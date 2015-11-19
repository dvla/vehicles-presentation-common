package uk.gov.dvla.vehicles.presentation.common.utils.helpers

import play.api.mvc.Results.Redirect
import play.api.mvc.{Call, DiscardingCookie, RequestHeader, Result}
import uk.gov.dvla.vehicles.presentation.common.model.SeenCookieMessageCacheKey

object CookieHelper {
  def discardAllCookies(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    val discardingCookiesKeys = request.cookies.map(_.name).filter(_ != SeenCookieMessageCacheKey)
    val discardingCookies = discardingCookiesKeys.map(DiscardingCookie(_)).toSeq
    Redirect(redirectRoute)
      .discardingCookies(discardingCookies: _*)
  }
}
