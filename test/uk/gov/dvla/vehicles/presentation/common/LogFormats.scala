package uk.gov.dvla.vehicles.presentation.common

import org.mockito.Mockito._
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId

class LogFormatsUnitSpec extends UnitSpec with DVLALogger  {
  val trackingId = TrackingId("test-trackingId")
  val message = "Test message to log"
  val logData = Some(List("one", "two", "three"))
  val expectedLogMessage = s"[TrackingID: test-trackingId]${LogFormats.logSeperator}$message"
  val expectedLogMessageWithlogData = s"$expectedLogMessage${LogFormats.logSeperator}Some(List(one, two, three))"

  "logMessage" should {
    "log correctly with Info" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Info, message)
      verify(logger).info(expectedLogMessage)
    }

    "log correctly with Debug" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Debug, message)
      verify(logger).debug(expectedLogMessage)
    }

    "log correctly with Error" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Error, message)
      verify(logger).error(expectedLogMessage)
    }

    "log correctly with Warn" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Warn, message)
      verify(logger).warn(expectedLogMessage)
    }

    "include logData when its included with Info" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Info, message, logData)
      verify(logger).info(expectedLogMessageWithlogData)
    }

    "include logData when its included with Debug" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Debug, message, logData)
      verify(logger).debug(expectedLogMessageWithlogData)
    }

    "include logData when its included with Error" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Error, message, logData)
      verify(logger).error(expectedLogMessageWithlogData)
    }

    "include logData when its included with Warn" in {
      implicit val logger = mock[org.slf4j.Logger]

      logMessage(trackingId, Warn, message, logData)
      verify(logger).warn(expectedLogMessageWithlogData)
    }
  }
  "Anonymize" should {
    "empty string should return null" in {
      val inputString: String = null
      LogFormats.anonymize(inputString) should equal("null")
    }

    "string of greater than 8 characters should return 4 characters and the rest stars" in {
      val inputString = "qwertyuiop"
      LogFormats.anonymize(inputString) should equal("******uiop")
    }

    "string of less than 8 characters should return half characters and the rest stars" in {
      val inputString = "qwer"
      LogFormats.anonymize(inputString) should equal("**er")
    }

    "string of 8 characters should return half characters and the rest stars" in {
      val inputString = "qwertyui"
      LogFormats.anonymize(inputString) should equal("****tyui")
    }

    "string with an odd number of characters should return more than half stars and the remainder characters" in {
      val inputString = "qwert"
      LogFormats.anonymize(inputString) should equal("***rt")
    }

    "string with 1 character should replace it with a star" in {
      val inputString = "q"
      LogFormats.anonymize(inputString) should equal("*")
    }

    "string with 4 characters should return 2 characters and the rest stars" in {
      val inputString = "qwer"
      LogFormats.anonymize(inputString) should equal("**er")
    }
  }
}