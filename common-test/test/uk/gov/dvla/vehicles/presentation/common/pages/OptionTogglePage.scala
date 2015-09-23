package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{WebDriver, By, SearchContext}
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.RadioButtonGroup
import org.scalatest.selenium.WebBrowser.tagName
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.OptionFieldSuffix
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form._

object OptionTogglePage extends Page {
  final val address = "/option-toggle"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Option Toggle"
  val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address
  implicit lazy val webDriver = WebDriverFactory.webDriver

  class OptionToggleWidget[T](widgetId: String, t: Element => T)
                             (driver: SearchContext) {
    private implicit def widget = driver.findElement(By.id(widgetId))

    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, WebDriverFactory.webDriver)

    def label: Element = find(tagName("legend"))//(widget)
      .getOrElse(throw new Exception(s"Cannot find name(legend element) for OptionToggleWidget with id:$widgetId"))

    def component(implicit driver: SearchContext): T = t(find(id(widgetId + OptionFieldSuffix)).getOrElse(
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

//  def textRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField](
//    OptionalStringOptionId,
//    optionalDiv => TextField(
//      find(id(OptionalStringId))(WebDriverFactory.webDriver).getOrElse(
//        throw new Exception(s"Cannot find component element withId: $OptionalStringId"))
//        .underlying
//    )
//  )(driver)

  def textRadio(implicit driver: SearchContext) = {
    println("---------")
    println (">>>> " + OptionalStringId)
    println (">>>> " + id(OptionalStringId))
    println (">>>> " + find(id(OptionalStringId)))
    println("---------")
    new OptionToggleWidget[TextField](
    OptionalStringOptionId,
    optionalDiv => textField(id(OptionalStringId))
  )(driver)
  }

//  def intRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField](
//    OptionalIntOptionId,
//    optionalDiv => TextField(find(id(OptionalIntId))(WebDriverFactory.webDriver).getOrElse(
//      throw new Exception(s"Cannot find component element withId: $OptionalIntId"))
//      .underlying
//    )
//  )(driver)

  def intRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField](
    OptionalIntOptionId,
    optionalDiv => textField(id(OptionalStringId))
  )(driver)

  def dateRadio(implicit driver: SearchContext) =
    new OptionToggleWidget[DateWidget](OptionalDateOptionId, _ => DateWidget(OptionalDateId))(driver)

  def submit(implicit driver: SearchContext): Element =  find(id("submit")).getOrElse {
    throw new Exception(s"Cannot find submit on the page")
  }
}
