package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import com.google.inject.Inject
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{booleanProp, getOptionalProperty, getProperty, intProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

trait CookieFlags {

  def applyToCookie(cookie: Cookie, key: String): Cookie

  def applyToCookie(cookie: Cookie): Cookie
}

final class NoCookieFlags extends CookieFlags {

  override def applyToCookie(cookie: Cookie, key: String): Cookie = cookie

  override def applyToCookie(cookie: Cookie): Cookie = applyToCookie(cookie, key = "")
}

final class CookieFlagsFromConfig @Inject()() extends CookieFlags {

  private val cookieMaxAgeSeconds = getProperty[Int]("application.cookieMaxAge")
  private val secureCookies = getOptionalProperty[Boolean]("secureCookies").getOrElse(CommonConfig.DEFAULT_SECURE_COOKIES)

  override def applyToCookie(cookie: Cookie, key: String = ""): Cookie =
    cookie.copy(
      secure = secureCookies,
      maxAge = Some(cookieMaxAgeSeconds)
    )

  override def applyToCookie(cookie: Cookie): Cookie = applyToCookie(cookie, key = "")
}
