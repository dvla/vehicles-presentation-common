package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{By, JavascriptExecutor, WebDriver}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}

object PleaseWaitPage extends Page {
  final val address = "/please-wait"
  val url: String = WebDriverFactory.testUrl + address.substring(1)
  val title: String = "Please Wait"

  val pleaseWaitOverlayCssSelector = ".please-wait-overlay"
  val pleaseWaitCloseAnchorCssSelector  =  ".please-wait-overlay a"

  def toggleOverlay(implicit driver: WebDriver) = {
    val javascript = driver.asInstanceOf[JavascriptExecutor]
    javascript.executeScript(s"$$('$pleaseWaitOverlayCssSelector').toggle();")
  }

  def closeOverlay(implicit driver: WebDriver) = driver.findElement(By.cssSelector(pleaseWaitCloseAnchorCssSelector))



}