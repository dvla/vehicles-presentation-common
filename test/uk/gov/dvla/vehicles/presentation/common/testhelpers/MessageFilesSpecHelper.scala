package uk.gov.dvla.vehicles.presentation.common.testhelpers

import java.net.URL
import java.nio.file.Paths
import play.api.i18n.Messages.{messages, UrlMessageSource}
import play.api.PlayException

object MessageFilesSpecHelper {
  private val MESSAGES_FILE = "conf/messages"
  final val WELSH_FILE: URL = Paths.get(s"$MESSAGES_FILE.en").toUri.toURL
  final val ENGLISH_FILE: URL = Paths.get(s"$MESSAGES_FILE.cy").toUri.toURL

  final val messagesFilesHelper: MessageFilesSpecHelper = new MessageFilesSpecHelper
}

class MessageFilesSpecHelper {

  def parse(url: URL): Either[PlayException.ExceptionSource, Map[String, String]] =
    messages(UrlMessageSource(url), url.getFile)

  private def count(m1: Map[String, String],
                    m2: Map[String, String],
                    condition: (String, String) => Boolean): Integer =
    m1.foldLeft(0) { case (acc, (k, v)) =>
      if (condition(v, m2.getOrElse(k, ""))) {
        println(s"key $k, m1: $v, m2: ${m2.getOrElse(k, "")}")
        acc + 1
      } else {
        acc
      }
    }

  // count increments if vals are different lengths
  def getNonBlankValuesCount(m1: Map[String, String], m2: Map[String, String]): Integer =
    count(m1, m2, (v1, v2) => v1.length == 0 && v2.length != 0)

  def getBlankValuesCount(m1: Map[String, String], m2: Map[String, String]): Integer =
    count(m1, m2, (v1, v2) => v1.length == 0 && v2.length == 0)
}
