package uk.gov.dvla.vehicles.presentation.common

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
      case Some(i) => anonymize(i.toString)
      case None => nullString
    }
  }

  def logMessage(messageText: String, trackingId: String, logData: Seq[String]): String =
    messageText + logSeperator + logData + "trackingId: " + trackingId

  def logMessage(messageText: String, trackingId: String): String =
    messageText + logSeperator + "trackingId: " + trackingId

}
