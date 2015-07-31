package uk.gov.dvla.vehicles.presentation.common

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

object LogFormats {

  private final val anonymousChar = "*"
  private final val logSeperator = " - "
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

  // When changes these two logMessage methods, be sure to replicate the changes in
  //    vehicles-services-common/src/main/scala/dvla.common/LogFormats.scala
  // in order to keep a consistent log format
  def logMessage(messageText: String, trackingId: TrackingId, logData: Seq[String]): String =
    messageText + logSeperator + logData + "trackingId: " + trackingId.value

  def logMessage(messageText: String, trackingId: TrackingId): String =
    messageText + logSeperator + "trackingId: " + trackingId.value

}
