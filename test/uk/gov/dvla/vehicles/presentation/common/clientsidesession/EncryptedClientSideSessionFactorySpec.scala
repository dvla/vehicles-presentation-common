package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.TestWithApplication

class EncryptedClientSideSessionFactorySpec extends UnitSpec {
  implicit val noCookieFlags = new NoCookieFlags
  implicit val noEncryption = new NoEncryption with CookieEncryption
  implicit val noHashing = new NoHashGenerator with CookieNameHashGenerator
  val encryptedClientSideSessionFactory =
    new EncryptedClientSideSessionFactory()(noCookieFlags, noEncryption, noHashing) {
      override protected lazy val secureCookies: Boolean = true
      override protected lazy val sessionSecretKeySuffixKey: String = "FE291934-66BD-4500-B27F-517C7D77F26B"
    }

  "newSessionCookiesIfNeeded" should {

    "create a couple of session cookies if there are not yet in the request" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])

      cookies.get should have size 2
      cookies.get.head.value should have size 20
      cookies.get.last.value should not be empty
    }

    "returns None if the cookies are already present in the request" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      val cookies2 = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(cookies.get)
      cookies2 should be(None)
    }

    "explode with an exception if there is only the trackingId cookie present" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      intercept[InvalidSessionException] {
        encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq(cookies.get.head))
      }
    }

    "explode with an exception if there is only the session cookie present" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      intercept[InvalidSessionException] {
        encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq(cookies.get(1)))
      }
    }

    "explode with an exception if the session cookies are fake or not properly encrypted" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      val trackingIdCookie = cookies.get.head
      val sessionCookie = cookies.get.last
      val fakeTrakingIdCookie = Cookie(trackingIdCookie.name, "x" * 20)
      val fakeSessionIdCookie = Cookie(sessionCookie.name, "x" * 40)

      intercept[InvalidSessionException] {
        encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq(fakeTrakingIdCookie, fakeSessionIdCookie))
      }
    }
  }

  "getSession" should {
    "get the session from the request cookies" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      val session = encryptedClientSideSessionFactory.getSession(cookies.get)
      session shouldBe an[EncryptedClientSideSession]

      val trackingIdCookie = cookies.get.head
      val sessionCookie = cookies.get(1)

      session.trackingId.value should equal(trackingIdCookie.value)

      val encryptedSessionKey = session.getCookieValue(
        Cookie(sessionCookie.name, trackingIdCookie.value + sessionCookie.value))

      encryptedSessionKey should not be empty
    }

    "explode with an exception if the session cookies are fake or not properly encrypted" in new TestWithApplication {
      val cookies = encryptedClientSideSessionFactory.newSessionCookiesIfNeeded(Seq.empty[Cookie])
      val trackingIdCookie = cookies.get.head
      val sessionCookie = cookies.get.last
      val fakeTrackingIdCookie = Cookie(trackingIdCookie.name, "x" * 20)
      val fakeSessionIdCookie = Cookie(sessionCookie.name, "x" * 40)

      intercept[InvalidSessionException] {
        encryptedClientSideSessionFactory.getSession(Seq(fakeTrackingIdCookie, fakeSessionIdCookie))
      }
    }

    "explode with an exception if the are no session cookies in the request" in new TestWithApplication {
      intercept[InvalidSessionException] {
        encryptedClientSideSessionFactory.getSession(Seq.empty[Cookie])
      }
    }
  }
}