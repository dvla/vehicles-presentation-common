package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import play.api.GlobalSettings
import play.api.test.{FakeApplication, TestServer, _}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication


// NOTE: Do *not* put any initialisation code in the class below, otherwise delayedInit() gets invoked twice
// which means around() gets invoked twice and everything is not happy.  Only lazy vals and defs are allowed,
// no vals or any other code blocks.

trait TestHarnessBase extends ProgressBar with GlobalCreator {
  import WebBrowser._

  abstract class WebBrowserForSelenium(val app: FakeApplication = fakeAppWithTestGlobal,
                                       val port: Int = testPort,
                                       implicit protected val webDriver: WebDriver = WebDriverFactory.webDriver)
    extends Around with Scope {
//    extends Around with Scope with WebBrowser{

    override def around[T: AsResult](t: => T): Result =
      TestConfiguration.configureTestUrl(port) {
        try Helpers.running(TestServer(port, app))(AsResult.effectively(t))
        finally webDriver.quit()
      }
  }

  abstract class ProgressBarTrue extends WebBrowserForSelenium(app = fakeApplicationWithProgressBarTrue)

  abstract class ProgressBarFalse extends WebBrowserForSelenium(app = fakeApplicationWithProgressBarFalse)

  abstract class WebBrowserWithJs extends WebBrowserForSelenium(
    webDriver = WebDriverFactory.webDriver(javascriptEnabled = true)
  )

  abstract class WebBrowserWithJsDisabled extends WebBrowserForSelenium(
    webDriver = WebDriverFactory.webDriver(javascriptEnabled = false)
  )

  abstract class PhantomJsByDefault extends WebBrowserForSelenium(
    webDriver = WebDriverFactory.defaultBrowserPhantomJs
  )

  object WebBrowser {

    private[TestHarnessBase] lazy val fakeAppWithTestGlobal: FakeApplication = LightFakeApplication(global)
    private[TestHarnessBase] lazy val testPort: Int = TestConfiguration.testPort
  }

}

trait GlobalCreator {
  def global: GlobalSettings
}
