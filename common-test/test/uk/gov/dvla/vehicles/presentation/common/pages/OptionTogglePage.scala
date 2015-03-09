package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{By, SearchContext}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser._
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.OptionFieldSuffix
import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form._

object OptionTogglePage extends Page with WebBrowserDSL {
  final val address = "/option-toggle"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Option Toggle"

  class OptionToggleWidget[T](widgetId: String, t: Element => T)
                             (driver: SearchContext) extends WebBrowserDSL {
    private implicit def widget = driver.findElement(By.id(widgetId))

    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, widget)

    def label: Element = find(tagName("legend"))(widget)
      .getOrElse(throw new Exception(s"Cannot find name(legend element) for OptionToggleWidget with id:$widgetId"))

    def component(implicit driver: SearchContext): T = t(find(id(widgetId + OptionFieldSuffix)).getOrElse(
      throw new Exception(s"Cannot find component element withId: ${widgetId}$OptionFieldSuffix " +
        s"for OptionToggleWidget with id:$widgetId"))
    )
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
