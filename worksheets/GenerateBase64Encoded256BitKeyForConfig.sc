/**
 * Script to generate a cryptographically secure 256-bit key, which is then base-64 encoded.
 *
 * We are using this as an application-scoped secret key (i.e. the same key must be used across all application instances).
 *
 * The Play Framework already provides an application-scoped secret key which is stored as a string in the config file.
 *
 * However, the current Play Framework implementation performs a direct character-to-byte conversion. Since a character
 * is limited in the range of bits it can represent (most obviously: the null-terminating character cannot be used)
 * the amount of information expressed in each byte (post 'decode') will be fewer than 256 bits.
 *
 * Simply put: each byte in a Play Framework secret key uses fewer than 256 possible combinations.
 *
 * Our solution: generate a cryptographically secure array of 32 bytes and base-64 encode it. This gives us a string which
 * can be saved in the config, which can be decoded to an array of bytes, each using the full 256 bit possible combinations.
 */

import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64

val keySizeInBits = 256

def getSecureRandomBytes(numberOfBytes: Int): Array[Byte] = {
  val random = new SecureRandom()
  val bytes = new Array[Byte](numberOfBytes)
  random.nextBytes(bytes)
  bytes
}

val secretKey = getSecureRandomBytes(keySizeInBits / 8)
val base64EncodedSecretKey = Base64.encodeBase64String(secretKey)
val secretLine = s"""application.secret256Bit = "$base64EncodedSecretKey""""
val newGuid = java.util.UUID.randomUUID().toString
val sessionLine = s"""application.sessionSecretKeySuffixKey = "$base64EncodedSecretKey""""

println(s"\n\nAdd/overwrite the following lines in the encrypted config:\n\n$secretLine\n\n$sessionLine")

