package uk.gov.dvla.vehicles.presentation.common.testhelpers

import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import MessageFilesSpecHelper.messagesFilesHelper

final class MessageFilesHelperSpec extends UnitSpec with UnitTestHelper {

  val TESTFILE1 = "test/resources/messagesTest1.txt"
  val TESTFILE2 = "test/resources/messagesTest2.txt"

  "Message files" should {
    val file1Keys = messagesFilesHelper.extractMessageKeys(TESTFILE1)
    val file2Keys = messagesFilesHelper.extractMessageKeys(TESTFILE2)
    val mapFile1 = messagesFilesHelper.extractMessageMap(TESTFILE1)
    val mapFile2 = messagesFilesHelper.extractMessageMap(TESTFILE2)

    "have a corresponding File2 key for each File1 key" in {
      val file1KeysWithNoFile2Equivalent = file1Keys.filterNot(enKey => file2Keys.contains(enKey))
      println(s"File1 keys that are missing in the File2 message file: $file1KeysWithNoFile2Equivalent")
      file1KeysWithNoFile2Equivalent should equal(List("test.six"))
    }

    "have a corresponding File1 key for each File2 key" in {
      val File2KeysWithNoFile1Equivalent = file2Keys.filterNot(cyKey => file1Keys.contains(cyKey))
      println(s"File2 keys that are missing in the File1 message file: $File2KeysWithNoFile1Equivalent")
      File2KeysWithNoFile1Equivalent should equal(List.empty)
    }

    "have an File1 value and a corresponding non-blank File2 value" in {
      messagesFilesHelper.getBlankNonBlankValuesCount(mapFile1, mapFile2) should equal(1)
    }

    "have a File2 value and a corresponding non-blank File1 value" in {
      messagesFilesHelper.getBlankNonBlankValuesCount(mapFile2, mapFile1) should equal(1)
    }

  }
}
