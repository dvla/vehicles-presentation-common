package views

import helpers.UiSpec
import helpers.webbrowser.TestHarness
import pages.VersionPage
import scala.io.Source.fromInputStream

class VersionIntegrationSpec extends UiSpec with TestHarness {
  "Version endpoint" should {
    "redirects to the next page given valid input" in new WebBrowser {
      go.to(VersionPage)
      val t = fromInputStream(getClass.getResourceAsStream("/build-details.txt")).getLines().toList
      t.filterNot(page.source.lines.contains(_)) should be(empty)
    }
  }
}
