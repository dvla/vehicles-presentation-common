package uk.gov.dvla.vehicles.presentation.common.testhelpers

import scala.collection.breakOut

object MessageFilesSpecHelper {
  private val MESSAGES_FILE = "conf/messages"
  final val WELSH_FILE = MESSAGES_FILE + ".en"
  final val ENGLISH_FILE = MESSAGES_FILE + ".cy"

  final val messagesFilesHelper : MessageFilesSpecHelper = new MessageFilesSpecHelper
}

class MessageFilesSpecHelper {

  def getLines(file: String): List[String] = {
    val source = scala.io.Source.fromFile(file)
    val lines: List[String] = source.getLines().filterNot(_.isEmpty).filterNot(_.startsWith("#")).toList
    source.close()
    lines
  }

  def extractMessageKeys(file: String): List[String] = {
    getLines(file).map(keyValue => keyValue.split("=").head.trim)
  }

  def extractMessageMap(file: String): Map[String, String] = {
    (extractMessageKeys(file) zip getLines(file).map(keyValue => keyValue.split("=").tail.mkString))(breakOut): Map[String,String]
  }

  // count increments if vals are different lengths
  def getBlankNonBlankValuesCount(m1: Map[String, String], m2: Map[String, String]): Integer = {
      var result = 0
      m1 foreach {
        case (m1key, m1value) => {
          val m2valFromm1Key = m2.get(m1key).getOrElse("")

          if (m1value.length == 0 && m2valFromm1Key.length != 0) {
            println(s"m1 value empty and m2 value not for key: $m1key , val: $m2valFromm1Key")
            result += 1
          }
        }
      }
      result
  }

}
