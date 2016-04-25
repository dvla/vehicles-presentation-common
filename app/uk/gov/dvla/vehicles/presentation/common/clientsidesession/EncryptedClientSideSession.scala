package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import play.api.mvc.Cookie

/**
  * This class is responsible for dealing with encrypted client side cookies
  *
  * @param trackingId the tracking id value
  * @param sessionSecretKey the session secret key value
  * @param cookieFlags an implementation of the CookieFlags trait that applies various cookie settings
  * @param encryption an implementation of the Encryption trait that will provide encryption/decryption capabilities
  * @param cookieNameHashGenerator implementation of the HashGenerator trait that implements a one-way hash
  */
class EncryptedClientSideSession(override val trackingId: TrackingId,
                                 val sessionSecretKey: String)
                                (implicit cookieFlags: CookieFlags,
                                 encryption: CookieEncryption,
                                 cookieNameHashGenerator: CookieNameHashGenerator) extends ClientSideSession {

  /**
    * Create a new CookieName instance consisting of a one-way hash of the cookie name prefixed
    * by the session secret key
    *
    * @param key the cookie name
    * @return a new CookieName instance that consists of a one-way hash of the cookie name prefixed by the session
    *         secret key
    */
  override def nameCookie(key: String): CookieName = CookieName(cookieNameHashGenerator.hash(sessionSecretKey + key))

  /**
    * Create a new play.api.mvc.Cookie instance whose value is the cookie's value prefixed by the name of
    * the cookie. We do this to couple the cookie value to the cookie name so a user is not able to copy
    * the contents of one cookie into a different cookie.
    *
    * @param name a CookieName instance
    * @param value the value that will be stored in the cookie
    * @param key the cookie name
    * @return a new play.api.mvc.Cookie instance
    */
  override def newCookie(name: CookieName, value: String, key: String): Cookie = {
    val nameCoupledToValue = name.value + value
    val cipherText = encryption.encrypt(nameCoupledToValue)
    // Create the new cookie with the appropriate cookie flags set (eg. max age, secure)
    cookieFlags.applyToCookie(
      Cookie(name = name.value, value = cipherText),
      key = key
    )
  }

  /**
    * Create a new play.api.mvc.Cookie instance, passing just the cookie name and value
    *
    * @param name a CookieName instance
    * @param value the value that will be stored in the cookie
    * @return a new play.api.mvc.Cookie instance
    */
  override def newCookie(name: CookieName, value: String): Cookie = newCookie(name, value, key = "not-set")

  /**
    * The cookie payload consists of the encrypted concatenation of the cookie name followed by the cookie value
    * This method decrypts the payload and then separates it into the cookie name and cookie value. The cookie
    * value is then returned. If the cookie name from the payload does not match the name of the cookie an
    * AssertionError will be thrown
    *
    * @param cookie the cookie whose value we want to fetch
    * @return the unencrypted cookie value
    */
  override def getCookieValue(cookie: Cookie): String = {
    val cookieName = cookie.name
    val cipherText = cookie.value
    val valueCoupledToName = encryption.decrypt(cipherText)
    val (cookieNameFromPayload, value) = valueCoupledToName.splitAt(cookieName.length)
    assert(cookieName == cookieNameFromPayload, "The cookie name bytes from the payload must match the cookie name")
    value
  }

  override def equals(other: Any): Boolean = other match {
    case o: EncryptedClientSideSession if this eq o => true
    case o: EncryptedClientSideSession => this.sessionSecretKey == o.sessionSecretKey
    case _ => false
  }

  override def hashCode(): Int = this.sessionSecretKey.hashCode
}
