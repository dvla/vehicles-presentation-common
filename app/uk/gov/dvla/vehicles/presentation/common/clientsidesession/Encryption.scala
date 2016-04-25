package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import org.apache.commons.codec.binary.Base64
import play.api.libs.Codecs
import play.api.Play

trait Encryption {
  /**
    * Apply encryption algorithm to transform the cipher text into clear text
    *
    * @param cipherText the encrypted text to transform into clear text
    * @return clear text
    */
  def decrypt(cipherText: String): String
  /**
    * Apply encryption algorithm to transform the clear text into cipher text
    *
    * @param clearText the clear text to transform into encrypted text
    * @return cipher text
    */
  def encrypt(clearText: String): String
}

trait CookieEncryption extends Encryption

trait HashGenerator {
  val digestStringLength: Int

  def hash(clearText: String): String
}

trait CookieNameHashGenerator extends HashGenerator

class Sha1HashGenerator extends HashGenerator {
  private final val Sha1SizeInBits = 160
  private final val BitsPerHexCharacter = 4
  private final val CharactersInHexedSha1 = Sha1SizeInBits / BitsPerHexCharacter

  override val digestStringLength: Int = CharactersInHexedSha1

  override def hash(clearText: String): String = Codecs.sha1(clearText)
}

class NoHashGenerator extends HashGenerator {
  override def hash(clearText: String): String = clearText

  override val digestStringLength: Int = 0
}

/**
  * Advanced Encryption Standard implementation of the Encryption trait, which uses a symmetric
  * encryption algorithm to encrypt and decrypt the data
  */
class AesEncryption extends Encryption {
  // We are currently using AES 128 bit encryption (uses a 16 byte key (16 * 8 = 128 bit)).
  // In order to use AES 256 bit (uses a 32 byte key (32 * 8 = 256 bit)) you must install the unlimited strength policy
  // jar files into the jre and swap the lines below
  //  private val secretKey256Bit = applicationSecretKey256Bit.take(256 / 8)
  private val secretKey128Bit = applicationSecretKey256Bit.take(128 / 8)

  private lazy val applicationSecretKey256Bit: Array[Byte] = {
    val configKey = "application.secret256Bit"
    getConfig(configKey) match {
      case Some(base64EncodedApplicationSecret) =>
        val applicationSecret = Base64.decodeBase64(base64EncodedApplicationSecret)
        val keySizeInBits = 256
        val decodedKeySizeInBytes = keySizeInBits / 8

        if (applicationSecret.length != decodedKeySizeInBytes)
          throw new Exception(
            s"Application secret key must be $keySizeInBits" +
              s" bits ($decodedKeySizeInBytes decoded bytes). " +
              s"Actual size in bytes was ${applicationSecret.length}."
          )

        applicationSecret
      case None =>
        throw new Exception(s"Missing $configKey from config")
    }
  }

  private lazy val provider: Option[String] = getConfig("application.crypto.provider")
  // A transformation is a string that describes the operation (or set of operations) to be performed on the given input,
  // to produce some output. A transformation always includes the name of a cryptographic algorithm (e.g., AES),
  // and may be followed by a feedback mode and padding scheme.
  // eg AES/CBC/PKCS5Padding
  //
  // CBC (Cypher Block Chaining) uses an IV (initialisation vector or salt)
  private lazy val transformation: String = getConfig("application.crypto.aes.transformation").getOrElse("AES")
  private final val AesBlockSize = 128
  private final val InitializationVectorSizeInBytes = AesBlockSize / 8
  // We are using 128 bit key for our AES transformation
  private lazy val secretKeySpec = new SecretKeySpec(secretKey128Bit, "AES")

  /**
    * Decrypt the cipher text. The payload is as follows - base64 encoding of 2 sets of bytes concatenated together:
    * ([16 random IV bytes][AES encrypted cipher text bytes])
    * when decrypting AES CBC cipher text you need to use the same IV (salt) as you used when encrypting the data so
    * the payload contains the 16 random IV bytes that were used for encrypting so we can use them for decrypting
    *
    * @param cipherText the encrypted text to transform into clear text
    * @return clear text
    */
  override def decrypt(cipherText: String): String = {
    val initializationVectorWithCipherBytes = Base64.decodeBase64(cipherText)
    val (initializationVectorBytes, cipherBytes) =
      initializationVectorWithCipherBytes.splitAt(InitializationVectorSizeInBytes)
    val initializationVector = new IvParameterSpec(initializationVectorBytes)
    val cipher = getCryptographicCipher(transformation, provider)
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, initializationVector)
    val clearTextBytes = cipher.doFinal(cipherBytes)
    new String(clearTextBytes, StandardCharsets.UTF_8)
  }

  /**
    * Encrypts the supplied clear text, using AES symmetric transformation, which means the same key is used
    * to encrypt and decrypt.
    * We use AES with Cipher Block Chaining, which requires an IV (initialisation vector or salt)
    * We also need the IV when it comes to decrypting so it is prepended to the cipher text and base64 encoded
    * before being returned.
    *
    * @param clearText the clear text to transform into encrypted text
    * @return cipher text
    */
  override def encrypt(clearText: String): String = {
    val initializationVectorBytes = ByteGenerator.getSecureRandomBytes(InitializationVectorSizeInBytes)
    val initializationVector = new IvParameterSpec(initializationVectorBytes)
    val cipher = getCryptographicCipher(transformation, provider)
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, initializationVector)
    val clearTextBytes = clearText.getBytes(StandardCharsets.UTF_8)
    val cipherBytes = cipher.doFinal(clearTextBytes)
    Base64.encodeBase64String(initializationVectorBytes ++ cipherBytes)
  }

  /**
    * Returns a cryptographic cipher object for encryption/decryption that implements the specified transformation
    * Optionally, the name of a provider may be specified
    *
    * @param transformation the transformation to use eg. AES/CBC/PKCS5Padding
    * @param provider optional provider
    * @return a cryptographic cipher object for encryption/decryption
    */
  private def getCryptographicCipher(transformation: String, provider: Option[String]): Cipher =
    provider.fold(Cipher.getInstance(transformation))(p => Cipher.getInstance(transformation, p))

  private def getConfig(key: String) = Play.maybeApplication.flatMap(_.configuration.getString(key))
}

/**
  * No encryption implementation of the Encryption trait, used in testing
  */
class NoEncryption extends Encryption {
  override def decrypt(clearText: String): String = clearText
  override def encrypt(clearText: String): String = clearText
}
