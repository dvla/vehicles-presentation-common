package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import com.google.inject.Inject
import org.apache.commons.codec.binary.Hex
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import common.ConfigProperties.{booleanProp, getOptionalProperty, getProperty, intProp, stringProp}
import uk.gov.dvla.vehicles.presentation.common.utils.helpers.CommonConfig

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
  protected lazy val secureCookies: Boolean = getOptionalProperty[Boolean]("secureCookies").getOrElse(CommonConfig.DEFAULT_SECURE_COOKIES)
  protected lazy val sessionSecretKeySuffixKey: String = getProperty[String](SessionSecretKeySuffixKey)

  /**
    * This method is responsible for checking to see if the session cookies are present in the request object.
    * If they are missing this method will create them and return them in an Option.
    *
    * @param request Collection of cookies sent from the client's browser
    * @return An Option that may contain a collection of cookies.
    *         If the Option is filled that indicates that the session cookies were missing from the request
    *         and the cookies in the Option need to be added to the request.
    *         Otherwise if the Option is empty that indicates that the session cookies were found and
    *         nothing more needs to be done.
    */
  override def newSessionCookiesIfNeeded(request: Traversable[Cookie]): Option[Seq[Cookie]] =
    validateSessionCookies(request) match {
      // The session cookies have been found so return empty Option to indicate no further action is needed
      case Some((trackingId, sessionSecretKey)) => None
      // The session cookies have not been found so create them here and return them as a filled Option to the caller
      // so they can be added to the existing request cookies
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

  /**
    * Return the ClientSideSession object
    *
    * @param request The collection of cookies sent by the browser in the request object
    * @return A ClientSideSession object
    */
  override def getSession(request: Traversable[Cookie]): ClientSideSession =
    validateSessionCookies(request) match {
      case Some((trackingId, sessionSecretKey)) => new EncryptedClientSideSession(TrackingId(trackingId), sessionSecretKey)
      case _ => throw new InvalidSessionException("No session present in the request")
    }

  /**
    * Tries to find the two session cookies, which are the tracking id cookie and the session secret cookie
    *
    * @param requestCookies The collection of cookies sent by the browser in the request object
    * @return An Option of a tuple. If the cookies are located then a filled Option of a tuple is returned.
    *         The first value in the tuple is the trackingId cookie value and the second is the
    *         sessionSecretKeySuffixCookie value. If no cookies are found then None is returned. If only
    *         one cookie is located then an InvalidSessionException is thrown
    */
  private def validateSessionCookies(requestCookies: Traversable[Cookie]): Option[(String, String)] = {
    // Locate the tracking id cookie if it exists
    val trackingIdCookie: Option[Cookie] = requestCookies.find(_.name == ClientSideSessionFactory.TrackingIdCookieName)
    // The hash of the value read from config for the key: application.sessionSecretKeySuffixKey
    val sessionSecretKeySuffixCookieName = createSessionSecretKeySuffixCookieName
    // Locate the session secret key suffix cookie if it exists
    val sessionSecretKeySuffixCookie = requestCookies.find(_.name == sessionSecretKeySuffixCookieName)

    (trackingIdCookie, sessionSecretKeySuffixCookie) match {
      case (Some(trackingId), Some(sessionSecretKeySuffix)) => // Both cookies have been located
        // The cipher values from both cookies are concatenated and decrypted, which is the opposite
        // of how they were populated. The 2 session cookies have to exist as a pair because their
        // concatenated values are encrypted, split into 2 parts and each part is stored in the two
        // session cookies. The opposite happens here when their values are read back
        val decrypted = encryption.decrypt(trackingId.value + sessionSecretKeySuffix.value)
        // Split the clear text into a tuple of (the cookie name/session secret key)
        val (cookieNameFromPayload, sessionSecretKey) = decrypted.splitAt(sessionSecretKeySuffixCookieName.length)

        // Check that the cookie name that has been extracted from the cookie payload matches the cookie name
        // This prevents users from trying to paste the contents of one cookie into another cookie.
        // It effectively ties the cookie payload to the cookie name
        if (sessionSecretKeySuffixCookieName != cookieNameFromPayload) {
          val msg = "The cookie name bytes from the payload must match the cookie name - " +
            s"[TrackingId: ${trackingId.value}]"
          throw new InvalidSessionException(msg)
        }
        // Return a populated Option containing a tuple of plain text values read from the tracking id cookie and the
        // session secret cookie
        Some((trackingId.value, sessionSecretKey))
      case (None, None) => None // Neither cookie was found in the request so return back None
      case _ =>
        val msg = "Invalid session cookies coming from the request - expected 2 but only found 1"
        throw new InvalidSessionException(msg)
    }
  }

  /**
    * Returns a hash of the value read out of config for property application.sessionSecretKeySuffixKey
    * whose value contains 36 characters. After the one-way hash has been applied 40 characters result
    *
    * @return a hash of the session secret key suffix value
    */
  private def createSessionSecretKeySuffixCookieName: String = cookieNameHashing.hash(sessionSecretKeySuffixKey)


  /**
    * This method creates a new random session secret key, which will last for the duration of the browser session,
    * or in other words until the browser is closed down
    *
    * @return a 32 byte hex encoded string of the random 16 byte array that is populated by SecureRandom
    */
  private def newSessionSecretKey: String = {
    val secretKeyLengthBytes = 16
    // The hex encoding will double this to 32 bytes
    Hex.encodeHexString(ByteGenerator.getSecureRandomBytes(secretKeyLengthBytes))
  }
}

object EncryptedClientSideSessionFactory {
  private final val SessionSecretKeySuffixKey = "application.sessionSecretKeySuffixKey"
  private final val SessionSecretKeySuffixDefaultValue = "FE291934-66BD-4500-B27F-517C7D77F26B"
  private final val TrackingIdCookieMaxAgeKey = "application.TrackingIdCookieMaxAge"
  private final val SessionCookieMaxAgeKey = "application.sessionCookieMaxAge"
}
