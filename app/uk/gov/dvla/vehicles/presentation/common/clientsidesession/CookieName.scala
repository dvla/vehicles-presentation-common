package uk.gov.dvla.vehicles.presentation.common.clientsidesession

/**
 * @param value Maps onto [play.api.mvc.Cookie.name]
 */
class CookieName private(val value: String) extends AnyVal

object CookieName {
  def apply(value: String): CookieName = new CookieName(value)
}