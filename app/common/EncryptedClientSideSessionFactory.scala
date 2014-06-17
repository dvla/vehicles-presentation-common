package common

import app.ConfigProperties._
import utils.helpers.{CookieEncryption, CookieNameHashing}
import org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import com.google.inject.Inject
import play.api.mvc.Cookie

class EncryptedClientSideSessionFactory @Inject()()(implicit cookieFlags: CookieFlags,
                                                    encryption: CookieEncryption,
                                                    cookieNameHashing: CookieNameHashing) extends ClientSideSessionFactory {

  /**
   * Session secret key must not expire before any other cookie that relies on it.
   */
  private final val SessionSecretKeyLifetime = None

  val secureCookies: Boolean = getProperty("secureCookies", default = true)
  val sessionSecretKeySuffixKey: String = getProperty("sessionSecretKeySuffixKey", "FE291934-66BD-4500-B27F-517C7D77F26B")

  override def newSessionCookiesIfNeeded(request: Traversable[Cookie]): Option[Seq[Cookie]] = {
    validateSessionCookies(request) match {
      case Some((trackingId, sessionSecretKey)) => None
      case _ =>
        val sessionSecretKey = newSessionSecretKey

        val trackingIdCookieName = ClientSideSessionFactory.TrackingIdCookieName
        val sessionSecretKeyCipherText = encryption.encrypt(createSessionSecretKeySuffixCookieName + sessionSecretKey)
        val (prefixValue, suffixValue) = sessionSecretKeyCipherText.splitAt(20)

        val trackingIdCookie = Cookie(
          name = trackingIdCookieName,
          value = prefixValue,
          secure = secureCookies,
          maxAge = SessionSecretKeyLifetime)

        val sessionSecretKeySuffixCookie = Cookie(
          name = createSessionSecretKeySuffixCookieName,
          value = suffixValue,
          secure = secureCookies,
          maxAge = SessionSecretKeyLifetime)

        Some(Seq(trackingIdCookie, sessionSecretKeySuffixCookie))
    }
  }

  override def getSession(request: Traversable[Cookie]): ClientSideSession = {
    validateSessionCookies(request) match {
      case Some((trackingId, sessionSecretKey)) =>
        new EncryptedClientSideSession(trackingId, sessionSecretKey)
      case _ => throw new InvalidSessionException("No session present in the request")
    }
  }

  private def validateSessionCookies(requestCookies: Traversable[Cookie]): Option[(String, String)] = {
    val sessionSecretKeySuffixCookieName = createSessionSecretKeySuffixCookieName

    val trackingIdCookie = requestCookies.find(_.name == ClientSideSessionFactory.TrackingIdCookieName)
    val sessionSecretKeySuffixCookie = requestCookies.find(_.name == sessionSecretKeySuffixCookieName)

    (trackingIdCookie, sessionSecretKeySuffixCookie) match {
      case (Some(trackingId), Some(sessionSecretKeySuffix)) =>
        val decrypted = encryption.decrypt(trackingId.value + sessionSecretKeySuffix.value)
        val (cookieNameFromPayload, sessionSecretKey) = decrypted.splitAt(sessionSecretKeySuffixCookieName.length)
        if (sessionSecretKeySuffixCookieName != cookieNameFromPayload) {
          throw new InvalidSessionException("The cookie name bytes from the payload must match the cookie name")
        }
        Some((trackingId.value, sessionSecretKey))
      case (Some(trackingId), None) =>
        throw new InvalidSessionException("Invalid session cookies coming from the request")
      case (None, Some(cookie)) =>
        throw new InvalidSessionException("Invalid session cookies coming from the request")
      case _ => None
    }
  }

  private def createSessionSecretKeySuffixCookieName(implicit cookieNameHashing: CookieNameHashing): String =
    cookieNameHashing.hash(sessionSecretKeySuffixKey)

  private def newSessionSecretKey = Hex.encodeHexString(getSecureRandomBytes(16))

  private def getSecureRandomBytes(numberOfBytes: Int): Array[Byte] = {
    val random = new SecureRandom()
    val bytes = new Array[Byte](numberOfBytes)
    random.nextBytes(bytes)
    bytes
  }
}
