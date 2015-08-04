package uk.gov.dvla.vehicles.presentation.common

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import play.api.{LoggerLike, Logger}
import play.api.mvc.Request
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

object LogFormats {

  private final val anonymousChar = "*"
  private final val logSeperator = "\t"
  private final val nullString = "null"
  final val optionNone = "none"

  def anonymize(input: AnyRef): String = {
    if (input==null){
      nullString
    } else {
      val stringInput = input.toString
      val startOfNonAnonymizedText =
        if (stringInput.length == 0) 0
        else if (stringInput.length > 8) 4
        else stringInput.length / 2
      anonymousChar * (stringInput.length - startOfNonAnonymizedText) + stringInput.takeRight(startOfNonAnonymizedText)
    }
  }

  def anonymize(input: Option[_]): String = {
    input match {
      case null => nullString
      case Some(i) => anonymize(i.toString)
      case None => nullString
    }
  }


  trait DVLALogger {

    implicit val logger: org.slf4j.Logger = Logger.logger

    sealed trait LogMessageType
    case object Debug extends LogMessageType
    case object Info extends LogMessageType
    case object Error extends LogMessageType
    case object Warn extends LogMessageType

    def logMessage(trackingId: TrackingId, messageType: LogMessageType, messageText: String, logData: Option[Seq[String]] = None)
                  (implicit logger: org.slf4j.Logger) =
      messageType match {
        case Debug => logger.debug(logMessageFormat(trackingId, messageText, logData))
        case Info => logger.info(logMessageFormat(trackingId, messageText, logData))
        case Error => logger.error(logMessageFormat(trackingId, messageText, logData))
        case Warn => logger.warn(logMessageFormat(trackingId, messageText, logData))
      }


    private def logMessageFormat(trackingId: TrackingId, messageText: String, logData: Option[Seq[String]]): String =
      s"""[TrackingID: ${trackingId.value}]$logSeperator$messageText ${logData.map( d => s"$logSeperator$logData" ).getOrElse("")}"""
  }
  // When changes these two logMessage methods, be sure to replicate the changes in
  //    vehicles-services-common/src/main/scala/dvla.common/LogFormats.scala
  // in order to keep a consistent log format
//  def logMessage(messageText: String, trackingId: TrackingId, logData: Seq[String]): String =
//    messageText + logSeperator + logData + "trackingId: " + trackingId.value
//
//  def logMessage(messageText: String, trackingId: TrackingId): String =
//    messageText + logSeperator + "trackingId: " + trackingId.value

}
