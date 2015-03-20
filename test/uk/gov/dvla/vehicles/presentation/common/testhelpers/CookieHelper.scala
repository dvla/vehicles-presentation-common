package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.scalatest.Assertions.fail
import play.api.http.HeaderNames.SET_COOKIE
import play.api.mvc.{Cookie, Cookies, Result}

object CookieHelper {
   def fetchCookiesFromHeaders(result: Result): Seq[Cookie] =
     result.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)

   def verifyCookieHasBeenDiscarded(cookieName: String, cookies: Seq[Cookie]) =
     // A discarded cookie is identified by a negative maxAge
     cookies.find(_.name == cookieName).map(_.maxAge match {
       case Some(maxAge) if maxAge < 0 => // Success
       case Some(maxAge) => fail(s"maxAge for cookie $cookieName should be negative but was $maxAge")
       case _ => fail(s"cookie $cookieName has not been discarded. There is no max age set for that cookie.")
     }) orElse fail(s"cookie $cookieName has not been discarded. There is no cookies with such name.")

  def verifyCookieHasNotBeenDiscarded(cookieName: String, cookies: Seq[Cookie]) =
    // A discarded cookie is identified by a negative maxAge
    cookies.find(_.name == cookieName).map(_.maxAge match {
      case Some(maxAge) => fail("Should be no maxAge for a cookie that has not been discarded")
      case None => //Success
    })
 }
