package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{WebElement, By, SearchContext}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory

class AlertWarningPage(implicit driver: SearchContext) extends Page with WebBrowserDSL {

  final val address = "/alert-warning"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Alert Warning"

  private val warnings = driver.findElements(By.className("alert-warning"))
  lazy val warning1: WebElement = warnings.get(0)
  lazy val warning2: WebElement = warnings.get(1)
}

object AlertWarningPage {
  def instance(implicit driver: SearchContext) = new AlertWarningPage
}
