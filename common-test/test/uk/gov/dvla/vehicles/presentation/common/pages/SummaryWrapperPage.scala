package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.cssSelector
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import org.scalatest.selenium.WebBrowser.Element

object SummaryWrapperPage extends Page {
  private implicit val timeout = 10
  final val address = "/summary-wrapper"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary-Wrapper"

  final val contentCssSelector = ".details"
  final val showHideTriggerCssSelector = ".summary"

  def showHideTriggerElement()(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector(showHideTriggerCssSelector)) getOrElse(
      throw new Exception(s"Unable to find element for show/hide content")
      )
  }

}
