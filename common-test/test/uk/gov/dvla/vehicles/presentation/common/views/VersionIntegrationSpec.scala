package uk.gov.dvla.vehicles.presentation.common.views

import scala.io.Source.fromInputStream
import org.scalatest.selenium.WebBrowser.pageSource
import org.scalatest.selenium.WebBrowser.go
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.controllers.VersionControllerTest
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.VersionPage

class VersionIntegrationSpec extends UiSpec with TestHarness {

  "Version endpoint" should {
    "be declared and should include the build-details.txt from classpath" ignore new WebBrowserForSelenium {
      go.to(VersionPage)
      val t = fromInputStream(getClass.getResourceAsStream("/build-details.txt")).getLines().toSet.toList
      println("PAGE-SOURCE:" + pageSource)
      pageSource should contain allOf(t.head, t.tail.head, t.tail.tail.toSeq:_*)

      pageSource should include(VersionControllerTest.missingUrl)
      pageSource should include(VersionControllerTest.notParsingUrl)
    }
  }
}
