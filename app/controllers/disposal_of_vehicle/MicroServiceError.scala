package controllers.disposal_of_vehicle

import com.google.inject.Inject
import common.ClientSideSessionFactory
import common.CookieImplicits.{RichCookies, RichSimpleResult}
import controllers.disposal_of_vehicle.routes.VehicleLookup
import mappings.disposal_of_vehicle.MicroserviceError.MicroServiceErrorRefererCacheKey
import viewmodels.DisposeFormViewModel.PreventGoingToDisposePageCacheKey
import play.api.mvc.{Action, Controller}
import utils.helpers.Config

final class MicroServiceError @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                        config: Config) extends Controller {
  private final val DefaultRedirectUrl = VehicleLookup.present().url

  def present = Action { implicit request =>
    val referer = request.headers.get(REFERER).getOrElse(DefaultRedirectUrl)
    Ok(views.html.disposal_of_vehicle.micro_service_error()).
      // Save the previous page URL (from the referer header) into a cookie.
      withCookie(MicroServiceErrorRefererCacheKey, referer).
      // Remove the interstitial cookie so we do not get bounced back to vehicle lookup unless we were on that page
      discardingCookie(PreventGoingToDisposePageCacheKey)
  }

  def back = Action { implicit request =>
    val referer: String = request.cookies.getString(MicroServiceErrorRefererCacheKey).getOrElse(DefaultRedirectUrl)
    Redirect(referer).discardingCookie(MicroServiceErrorRefererCacheKey)
  }
}
