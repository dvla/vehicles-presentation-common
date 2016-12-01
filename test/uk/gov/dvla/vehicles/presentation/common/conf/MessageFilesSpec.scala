package uk.gov.dvla.vehicles.presentation.common.conf

import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.testhelpers.MessageFilesSpecHelper.ENGLISH_FILE
import uk.gov.dvla.vehicles.presentation.common.testhelpers.MessageFilesSpecHelper.messagesFilesHelper
import uk.gov.dvla.vehicles.presentation.common.testhelpers.MessageFilesSpecHelper.WELSH_FILE

class MessageFilesSpec extends UnitSpec {

  val englishMessages = messagesFilesHelper.parse(ENGLISH_FILE).right.get
  val welshMessages = messagesFilesHelper.parse(WELSH_FILE).right.get

  "Message files" should {
    "have a corresponding Welsh key for each English key" in {
      val englishKeysWithNoWelshEquivalent = englishMessages.filterNot { case (k, _) => welshMessages.contains(k) }
      println(s"English keys that are missing in the Welsh message file: $englishKeysWithNoWelshEquivalent")
      englishKeysWithNoWelshEquivalent should equal(Map.empty)
    }

    "have a corresponding English key for each Welsh key" in {
      val welshKeysWithNoEnglishEquivalent = welshMessages.filterNot { case (k, _) => englishMessages.contains(k) }
      println(s"Welsh keys that are missing in the English message file: $welshKeysWithNoEnglishEquivalent")
      welshKeysWithNoEnglishEquivalent should equal(Map.empty)
    }

    "have an English value and a corresponding non-blank Welsh value" in {
      messagesFilesHelper.getNonBlankValuesCount(englishMessages, welshMessages) should equal(0)
    }

    "have a Welsh value and a corresponding non-blank English value" in {
      messagesFilesHelper.getNonBlankValuesCount(welshMessages, englishMessages) should equal(0)
    }

    "have no blank Welsh and English values" in {
      messagesFilesHelper.getBlankValuesCount(welshMessages, englishMessages) should equal(0)
    }

  }
}
