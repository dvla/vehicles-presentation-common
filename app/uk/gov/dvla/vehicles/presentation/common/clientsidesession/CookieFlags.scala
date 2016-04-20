package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import com.google.inject.Inject
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getProperty, getOptionalProperty, intProp, booleanProp}

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
  private val secureCookies = getOptionalProperty[Boolean]("secureCookies").getOrElse(true)
//  private val cookieMaxAgeSeconds = getProperty("application.cookieMaxAge", 30.minutes.toSeconds.toInt)
//  private val secureCookies = getProperty("secureCookies", default = true)

  override def applyToCookie(cookie: Cookie, key: String = ""): Cookie =
    cookie.copy(
      secure = secureCookies,
      maxAge = Some(cookieMaxAgeSeconds)
    )

  override def applyToCookie(cookie: Cookie): Cookie = applyToCookie(cookie, key = "")
}
