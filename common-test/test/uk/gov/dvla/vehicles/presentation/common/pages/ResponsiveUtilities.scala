package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.{WebDriver, By}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.Wait

object ResponsiveUtilities {
  final val address = "/responsive-utilities"
  val url: String = WebDriverFactory.testUrl + address.substring(1)
  val title: String = "Responsive Utilities"
  val timeout: Int = 3

  def assertNoJsOnlyVisible(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".no-js-only")))
  }

  def assertNoJsOnlyInvisible(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".no-js-only")))
  }

  def assertJsOnlyVisible(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".js-only")))
  }

  def assertJsOnlyInvisible(implicit driver: WebDriver): Unit = {

    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".js-only")))

  }
}