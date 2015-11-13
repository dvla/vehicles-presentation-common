package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebDriverFactory, Page}
import org.openqa.selenium.{WebElement, By, WebDriver}
import org.scalatest.selenium.WebBrowser._
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.{OptionFieldSuffix}
import scala.collection.JavaConverters._

object OptionTogglePage extends Page {

  final val address = "/option-toggle"
  override val url: String = WebDriverFactory.testUrl + address
  val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address

  final override val title: String = "Option Toggle"

  class OptionToggleWidget[T](widgetId: String, t: Element => T)(implicit driver: WebDriver) {
    private implicit def widget = driver.findElement(By.id(widgetId.concat("-group")))
    private def inputRadioWithMatchingValue(webElement: WebElement, value: String): Boolean =
      webElement.getTagName.toLowerCase == "input" &&
        webElement.getAttribute("type").toLowerCase == "radio" &&
        webElement.getAttribute("value") == value

    def label = widget.findElement(By.tagName("legend"))
    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, driver)

    def radioInputWithValue(value: String) = {
      widget.findElements(By.name(widgetId)).asScala.toList.filter(inputRadioWithMatchingValue(_,value)).head
    }

    def component(implicit driver: WebDriver): T = t(find(id(widgetId + OptionFieldSuffix)).getOrElse(
      throw new Exception(s"Cannot find component element withId: $widgetId$OptionFieldSuffix " +
        s"for OptionToggleWidget with id:$widgetId"))
    )

    def assetComponentInvisible(implicit driver: WebDriver) =
      new WebDriverWait(driver, 3)
        .until(ExpectedConditions.invisibilityOfElementLocated(By.id(widgetId + OptionFieldSuffix)))

    def assetComponentVisible(implicit driver: WebDriver) =
      new WebDriverWait(driver, 3)
        .until(ExpectedConditions.visibilityOfElementLocated(By.id(widgetId + OptionFieldSuffix)))
  }

  def textRadioOption(implicit driver: WebDriver) =
    new OptionToggleWidget[TextField]("optional-string-option", optionalDiv => textField(id("optional-string")))

  def intRadioOption(implicit driver: WebDriver) =
    new OptionToggleWidget[TextField]("optional-int-option", optionalDiv => textField(id("optional-int")))

  def dateRadioOption(implicit driver: WebDriver) =
    new OptionToggleWidget[DateWidget]("optional-date-option", _ => DateWidget("optional-date"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).getOrElse {
    throw new Exception(s"Cannot find submit on the page")
  }
}