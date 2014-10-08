package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import models.TitlePickerModel.Form.TitleId
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import uk.gov.dvla.vehicles.presentation.common.mappings.TitlePickerString.OtherTitleRadioValue

object TitlePickerPage extends Page with WebBrowserDSL with Matchers {

  final val address = "/title-picker"
  override val url = WebDriverFactory.testUrl + address.substring(1)
  final override val title = "Title Picker Input"

  def mr(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("mr")}"))
  def miss(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("miss")}"))
  def mrs(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("mrs")}"))
  def other(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_$OtherTitleRadioValue"))
  def otherText(implicit driver: WebDriver) = textField(id(s"${TitleId}_titleText"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def radioButtons(implicit driver: WebDriver) = Seq(mr, miss, mrs, other)

  def assertEnabled()(implicit driver: WebDriver): Unit = {
    radioButtons.foreach(_.isEnabled should equal(true))
  }

  def select(title: String)(implicit driver: WebDriver): Unit = {
    radioButtons.find(_.underlying.getAttribute("id") endsWith titleType(title))
      .fold(throw new Exception(s"Radio button with id ending at $title not found"))(click on _)
  }

  def assertSelected(title: String)(implicit driver: WebDriver): Unit = {
    radioButtons.find(_.underlying.getAttribute("id") endsWith titleType(title))
      .fold(throw new Exception) ( _.isSelected should equal(true))
    radioButtons.filterNot(_.underlying.getAttribute("id") endsWith titleType(title))
      .map(_.isSelected should equal(false))
  }

  def assertNothingSelected()(implicit driver: WebDriver): Unit = {
    radioButtons.foreach(_.isSelected should equal(false))
  }

  def navigate(title: String, otherTitle: String)
              (implicit driver: WebDriver): Unit = {
    go to TitlePickerPage
    select(title)
    otherText.value = otherTitle
  }

  private def titleType(title: String): String = title match {
    case "mr" => "1"
    case "mrs" => "2"
    case "miss" => "3"
    case "other" => "4"
  }
}

