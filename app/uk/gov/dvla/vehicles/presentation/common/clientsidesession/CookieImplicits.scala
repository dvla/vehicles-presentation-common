package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.Logger
import play.api.data.Form
import play.api.http.HeaderNames
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._

/**
 * These are adapters that add cookie methods to a number of Play Framework classes.
 */
object CookieImplicits {

  implicit class RichCookie(val cookie: Cookie) extends AnyVal {

    def withSecure(flag: Boolean): Cookie = cookie.copy(secure = flag)

    def withMaxAge(age: Int): Cookie = cookie.copy(maxAge = Some(age))

    def withDomain(domain: String) = {
      domain match {
        case "NOT FOUND" =>
          Logger.error("cross-domain cookies will not be set as the url is not set in the config")
          cookie
        case _ => cookie.copy(domain = Some(domain))
      }
    }
  }

  implicit class RichCookies[A](val requestCookies: Traversable[Cookie]) extends AnyVal {

    def getModel[B](implicit fromJson: Reads[B],
                    cacheKey: CacheKey[B],
                    clientSideSessionFactory: ClientSideSessionFactory): Option[B] = {
      val session = clientSideSessionFactory.getSession(requestCookies)
      val cookieName = session.nameCookie(cacheKey.value).value
      requestCookies.find(_.name == cookieName).map { cookie =>
        val json = session.getCookieValue(cookie)
        val parsed = Json.parse(json)
        val jsResult = Json.fromJson[B](parsed)
        jsResult.asEither match {
          case Left(errors) => throw JsonValidationException(errors)
          case Right(model) => model
        }
      }
    }

    def getString(key: String)(implicit clientSideSessionFactory: ClientSideSessionFactory): Option[String] = {
      val session = clientSideSessionFactory.getSession(requestCookies)
      val cookieName = session.nameCookie(key).value
      requestCookies.find(_.name == cookieName).map(session.getCookieValue)
    }

    def trackingId()(implicit clientSideSessionFactory: ClientSideSessionFactory): String =
      clientSideSessionFactory.getSession(requestCookies).trackingId

    def withSecure(flag: Boolean) = ???
  }

  implicit class RichResult(val inner: Result) extends AnyVal {

    // 'Ex' suffix to avoid conflict with play framework.
    def withCookiesEx(cookies: CookieKeyValue*)
                     (implicit request: Request[_],
                      clientSideSessionFactory: ClientSideSessionFactory): Result =
      (inner /: cookies)(_.withCookie(_))

    def withCookie[A](model: A)(implicit
                                toJson: Writes[A],
                                cacheKey: CacheKey[A],
                                request: Request[_],
                                clientSideSessionFactory: ClientSideSessionFactory): Result = {
      val json = Json.toJson(model).toString()
      withCookie(cacheKey.value, json)
    }

    def withCookie(cookie: CookieKeyValue)
                  (implicit request: Request[_],
                   clientSideSessionFactory: ClientSideSessionFactory): Result =
      withCookie(cookie.key, cookie.value)

    def withCookie(key: String, value: String)
                  (implicit request: Request[_],
                   clientSideSessionFactory: ClientSideSessionFactory): Result = {
      val session = clientSideSessionFactory.getSession(request.cookies)
      val cookieName = session.nameCookie(key)
      val cookie = session.newCookie(cookieName, value, key)
      inner.withCookies(cookie)
    }

    def discardingCookie(key: String)
                        (implicit request: Request[_],
                         clientSideSessionFactory: ClientSideSessionFactory): Result =
      discardingCookies(Set(key))

    def discardingCookies(keys: Set[String])
                         (implicit request: Request[_],
                          clientSideSessionFactory: ClientSideSessionFactory): Result = {
      val session = clientSideSessionFactory.getSession(request.cookies)
      val cookieNames = keys.map(session.nameCookie)
      val discardingCookies = cookieNames.map(name => DiscardingCookie(name.value)).toSeq
      inner.discardingCookies(discardingCookies: _*)
    }

    def cookies: Map[String, Cookie] = {
      Cookies(inner.header.headers.get(HeaderNames.SET_COOKIE)).cookies
    }
  }

  implicit class RichForm[A](val f: Form[A]) extends AnyVal {

    def fill()(implicit request: Request[_],
               fromJson: Reads[A],
               cacheKey: CacheKey[A],
               clientSideSessionFactory: ClientSideSessionFactory): Form[A] =
      request.cookies.getModel[A] match {
        case Some(v) => f.fill(v) // Found cookie so fill the form with the cached data.
        case _ => f // No cookie found so return a blank form.
      }
  }

}
