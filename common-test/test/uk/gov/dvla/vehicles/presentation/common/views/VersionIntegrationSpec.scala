package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec

import uk.gov.dvla.vehicles.presentation.common.pages
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.TestHarness
import pages.VersionPage
import scala.io.Source.fromInputStream

class VersionIntegrationSpec extends UiSpec with TestHarness {
  "Version endpoint" should {
    "be declared and should include the build-details.txt from classpath" in new WebBrowser {
      go.to(VersionPage)
      val t = fromInputStream(getClass.getResourceAsStream("/build-details.txt")).getLines().toList

      page.source.lines should contain allOf(t.head, t.tail.head, t.tail.tail)
    }
  }
}
