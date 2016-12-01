package uk.gov.dvla.vehicles.presentation.common.testhelpers

import java.nio.file.Paths

import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import MessageFilesSpecHelper.messagesFilesHelper

final class MessageFilesHelperSpec extends UnitSpec {

  val TEST_FILE1 = Paths.get("test/resources/messagesTest1.txt").toUri.toURL
  val TEST_FILE2 = Paths.get("test/resources/messagesTest2.txt").toUri.toURL

  "Message files" should {
    val messageFile1 = messagesFilesHelper.parse(TEST_FILE1).right.get
    val messageFile2 = messagesFilesHelper.parse(TEST_FILE2).right.get

    "have a corresponding File2 key for each File1 key" in {
      val file1KeysWithNoFile2Equivalent = messageFile1.filterNot { case (k, _) => messageFile2.contains(k) }
      println(s"File1 keys that are missing in the File2 message file: $file1KeysWithNoFile2Equivalent")
      file1KeysWithNoFile2Equivalent should equal(Map("test.six" -> ""))
    }

    "have a corresponding File1 key for each File2 key" in {
      val File2KeysWithNoFile1Equivalent = messageFile2.filterNot { case (k, _) => messageFile1.contains(k) }
      println(s"File2 keys that are missing in the File1 message file: $File2KeysWithNoFile1Equivalent")
      File2KeysWithNoFile1Equivalent should equal(Map.empty)
    }

    "have an File1 value and a corresponding non-blank File2 value" in {
      messagesFilesHelper.getNonBlankValuesCount(messageFile1, messageFile2) should equal(1)
    }

    "have a File2 value and a corresponding non-blank File1 value" in {
      messagesFilesHelper.getNonBlankValuesCount(messageFile2, messageFile1) should equal(1)
    }

    "have three blank values in map1 and map2" in {
      messagesFilesHelper.getBlankValuesCount(messageFile1, messageFile2) should equal(3)
    }

    "have no blank values in map2 and map1" in {
      messagesFilesHelper.getBlankValuesCount(messageFile2, messageFile1) should equal(2)
    }

  }
}
