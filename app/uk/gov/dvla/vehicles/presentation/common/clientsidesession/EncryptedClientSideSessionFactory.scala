package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import com.google.inject.Inject
import java.security.SecureRandom
import org.apache.commons.codec.binary.Hex
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import common.ConfigProperties.{getProperty, getOptionalProperty, stringProp, booleanProp, intProp}

class EncryptedClientSideSessionFactory @Inject()()
                                                 (implicit cookieFlags: CookieFlags,
                                                  encryption: CookieEncryption,
                                                  cookieNameHashing: CookieNameHashGenerator) extends ClientSideSessionFactory {
  import EncryptedClientSideSessionFactory._
  /**
   * Session secret key must not expire before any other cookie that relies on it.
   */
  private lazy val TrackingIdCookieMaxAge: Option[Int] = getOptionalProperty[Int](TrackingIdCookieMaxAgeKey)
  private lazy val SessionCookieMaxAge: Option[Int] = getOptionalProperty[Int](SessionCookieMaxAgeKey)
//  private val secureCookies: Boolean = getProperty[Boolean]("secureCookies", default = true)
  protected lazy val secureCookies: Boolean = getOptionalProperty[Boolean]("secureCookies").getOrElse(true)
  protected lazy val sessionSecretKeySuffixKey: String = getProperty[String](SessionSecretKeySuffixKey)
//private val sessionSecretKeySuffixKey: String =
//    getProperty(SessionSecretKeySuffixKey, SessionSecretKeySuffixDefaultValue)

  override def newSessionCookiesIfNeeded(request: Traversable[Cookie]): Option[Seq[Cookie]] =
    validateSessionCookies(request) match {
      case Some((trackingId, sessionSecretKey)) => None
      case _ =>
        val sessionSecretKey = newSessionSecretKey
        val sessionSecretKeyCipherText = encryption.encrypt(createSessionSecretKeySuffixCookieName + sessionSecretKey)
        val trackingIdLengthChars = 20
        val (prefixValue, suffixValue) = sessionSecretKeyCipherText.splitAt(trackingIdLengthChars)

        val trackingIdCookie = Cookie(
          name = ClientSideSessionFactory.TrackingIdCookieName,
          value = prefixValue,
          secure = secureCookies,
          maxAge = TrackingIdCookieMaxAge
        )

        val sessionSecretKeySuffixCookie = Cookie(
          name = createSessionSecretKeySuffixCookieName,
          value = suffixValue,
          secure = secureCookies,
          maxAge = SessionCookieMaxAge
        )

        Some(Seq(trackingIdCookie, sessionSecretKeySuffixCookie))
    }

  override def getSession(request: Traversable[Cookie]): ClientSideSession =
    validateSessionCookies(request) match {
      case Some((trackingId, sessionSecretKey)) => new EncryptedClientSideSession(TrackingId(trackingId), sessionSecretKey)
      case _ => throw new InvalidSessionException("No session present in the request")
    }

  private def validateSessionCookies(requestCookies: Traversable[Cookie]): Option[(String, String)] = {
    val trackingIdCookie = requestCookies.find(_.name == ClientSideSessionFactory.TrackingIdCookieName)
    val sessionSecretKeySuffixCookieName = createSessionSecretKeySuffixCookieName
    val sessionSecretKeySuffixCookie = requestCookies.find(_.name == sessionSecretKeySuffixCookieName)

    (trackingIdCookie, sessionSecretKeySuffixCookie) match {
      case (Some(trackingId), Some(sessionSecretKeySuffix)) =>
        val decrypted = encryption.decrypt(trackingId.value + sessionSecretKeySuffix.value)
        val (cookieNameFromPayload, sessionSecretKey) = decrypted.splitAt(sessionSecretKeySuffixCookieName.length)

        if (sessionSecretKeySuffixCookieName != cookieNameFromPayload)
          throw new InvalidSessionException("The cookie name bytes from the payload must match the cookie name")

        Some((trackingId.value, sessionSecretKey))
      case (None, None) => None
      case _ => throw new InvalidSessionException("Invalid session cookies coming from the request")
    }
  }

  private def createSessionSecretKeySuffixCookieName: String = cookieNameHashing.hash(sessionSecretKeySuffixKey)

  private def newSessionSecretKey: String = {
    val secretKeyLengthBytes = 16
    Hex.encodeHexString(getSecureRandomBytes(secretKeyLengthBytes))
  }

  private def getSecureRandomBytes(numberOfBytes: Int): Array[Byte] = {
    val random = new SecureRandom()
    val bytes = new Array[Byte](numberOfBytes)
    random.nextBytes(bytes)
    bytes
  }
}

object EncryptedClientSideSessionFactory {
  private final val SessionSecretKeySuffixKey = "application.sessionSecretKeySuffixKey"
  private final val SessionSecretKeySuffixDefaultValue = "FE291934-66BD-4500-B27F-517C7D77F26B"
  private final val TrackingIdCookieMaxAgeKey = "application.TrackingIdCookieMaxAge"
  private final val SessionCookieMaxAgeKey = "application.sessionCookieMaxAge"
}
