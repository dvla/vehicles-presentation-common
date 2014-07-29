package utils.helpers

import controllers.disposal_of_vehicle.routes
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc.DiscardingCookie
import play.api.mvc.RequestHeader
import play.api.mvc.SimpleResult
import viewmodels.SeenCookieMessageCacheKey

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CookieHelper {
  def discardAllCookies(implicit request: RequestHeader): Future[SimpleResult] = {
    Logger.warn("Removing all cookies except seen cookie."
      + " Has the application secret changed or has a user tampered with cookie contents ?")

    Future {
      val discardingCookiesKeys = request.cookies.map(_.name).filter(_ != SeenCookieMessageCacheKey)
      val discardingCookies = discardingCookiesKeys.map(DiscardingCookie(_)).toSeq
      Redirect(routes.BeforeYouStart.present())
        .discardingCookies(discardingCookies: _*)
    }
  }
}
