package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{WebDriver, By, SearchContext}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Element
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.RadioButtonGroup
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.TextField
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.OptionFieldSuffix
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalDateId
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalDateOptionId
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalIntId
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalIntOptionId
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalStringId
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form.OptionalStringOptionId

object OptionTogglePage extends Page with WebBrowserDSL {
  final val address = "/option-toggle"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Option Toggle"
  val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address

  class OptionToggleWidget[T](widgetId: String, t: Element => T)
                             (driver: SearchContext) extends WebBrowserDSL {
    private implicit def widget = driver.findElement(By.id(widgetId))

    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, widget)

    def label: Element = find(tagName("legend"))(widget)
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

  def textRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField](
    OptionalStringOptionId,
    optionalDiv => TextField(find(id(OptionalStringId))(optionalDiv.underlying).getOrElse(
      throw new Exception(s"Cannot find component element withId: $OptionalStringId"))
    )
  )(driver)

  def intRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField](
    OptionalIntOptionId,
    optionalDiv => TextField(find(id(OptionalIntId))(optionalDiv.underlying).getOrElse(
      throw new Exception(s"Cannot find component element withId: $OptionalIntId"))
    )
  )(driver)

  def dateRadio(implicit driver: SearchContext) =
    new OptionToggleWidget[DateWidget](OptionalDateOptionId, _ => DateWidget(OptionalDateId))(driver)

  def submit(implicit driver: SearchContext): Element =  find(id("submit")).getOrElse {
    throw new Exception(s"Cannot find submit on the page")
  }
}
