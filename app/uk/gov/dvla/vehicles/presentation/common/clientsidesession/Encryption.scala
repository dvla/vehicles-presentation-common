package uk.gov.dvla.vehicles.presentation.common.clientsidesession

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import org.apache.commons.codec.binary.Base64
import play.api.Play
import play.api.libs.Codecs

trait Encryption {
  def decrypt(cipherText: String): String

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

class AesEncryption extends Encryption {
  // TODO decide which strength of AES encryption to use
  // in order to use AES 256 bit (uses a 32 byte key (32 * 8 = 256 bit)) you must install the unlimited strength policy jar
  // files into the jre at the moment we are using AES 128 bit (uses a 16 byte key (16 * 8 = 128 bit))
  //  private val secretKey256Bit = applicationSecretKey256Bit.take(256 / 8)
  private val secretKey128Bit = applicationSecretKey256Bit.take(128 / 8)

  private lazy val applicationSecretKey256Bit: Array[Byte] = {
    val configKey = "application.secret256Bit"
    getConfig(configKey) match {
      case Some(base64EncodedApplicationSecret) =>
        val keySizeInBits = 256
        val decodedKeySizeInBytes = keySizeInBits / 8
        val applicationSecret = Base64.decodeBase64(base64EncodedApplicationSecret)

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

  private final val InitializationVectorSizeInBytes = 128 / 8
  private lazy val provider: Option[String] = getConfig("application.crypto.provider")
  private lazy val transformation: String = getConfig("application.crypto.aes.transformation").getOrElse("AES")
  private lazy val secretKeySpec = new SecretKeySpec(secretKey128Bit, "AES")

  override def decrypt(cipherText: String): String = {
    val initializationVectorWithCipherBytes = Base64.decodeBase64(cipherText)
    val (initializationVectorBytes, cipherBytes) =
      initializationVectorWithCipherBytes.splitAt(InitializationVectorSizeInBytes)
    val initializationVector = new IvParameterSpec(initializationVectorBytes)
    val cipher = provider.fold(Cipher.getInstance(transformation))(p => Cipher.getInstance(transformation, p))
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, initializationVector)
    val clearTextBytes = cipher.doFinal(cipherBytes)
    new String(clearTextBytes, StandardCharsets.UTF_8)
  }

  override def encrypt(clearText: String): String = {
    val initializationVectorBytes = getSecureRandomBytes(InitializationVectorSizeInBytes)
    val initializationVector = new IvParameterSpec(initializationVectorBytes)
    val cipher = provider.fold(Cipher.getInstance(transformation))(p => Cipher.getInstance(transformation, p))
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, initializationVector)
    val clearTextBytes = clearText.getBytes(StandardCharsets.UTF_8)
    val cipherBytes = cipher.doFinal(clearTextBytes)
    Base64.encodeBase64String(initializationVectorBytes ++ cipherBytes)
  }

  private def getSecureRandomBytes(numberOfBytes: Int): Array[Byte] = {
    val random = new SecureRandom()
    val bytes = new Array[Byte](numberOfBytes)
    random.nextBytes(bytes)
    bytes
  }

  private def getConfig(key: String) = Play.maybeApplication.flatMap(_.configuration.getString(key))
}

class NoEncryption extends Encryption {
  override def decrypt(clearText: String): String = clearText

  override def encrypt(clearText: String): String = clearText
}
