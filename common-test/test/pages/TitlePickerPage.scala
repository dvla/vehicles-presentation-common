package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import models.TitlePickerModel.Form.TitleId
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers

class TitlePickerPage(implicit driver: WebDriver) extends Page with WebBrowserDSL {

    final val address = "/title-picker"
    override val url: String = WebDriverFactory.testUrl + address.substring(1)
    final override val title: String = "Title Picker Input"

    lazy val titlePicker = new TitlePickerWidget(TitleId)

    def submit(implicit driver: WebDriver): Element = find(id("submit")).get
}

object TitlePickerPage extends WebBrowserDSL {
  def navigate(title: String, otherTitle: String)
              (implicit driver: WebDriver): TitlePickerPage = {
    val page = new TitlePickerPage()
    go to page
    page.titlePicker.select(title)
    page.titlePicker.otherText.value = otherTitle
    page
  }
}

class TitlePickerWidget(idStr: String)(implicit driver: WebDriver) extends WebBrowserDSL with Matchers {
  lazy val mr = radioButton(id(s"${idStr}_titleOption_titlePicker.mr"))
  lazy val miss = radioButton(id(s"${idStr}_titleOption_titlePicker.miss"))
  lazy val mrs = radioButton(id(s"${idStr}_titleOption_titlePicker.mrs"))
  lazy val other = radioButton(id(s"${idStr}_titleOption_titlePicker.other"))

  lazy val radioButtons = Seq(mr, miss, mrs, other)

  def assertEnabled(): TitlePickerWidget = {
    radioButtons.foreach(_.isEnabled should equal(true))
    this
  }

  def select(title: String): TitlePickerWidget = {
    radioButtons.find(_.underlying.getAttribute("id") endsWith title).fold(throw new Exception)(click on _)
    this
  }

  def assertSelected(title: String): TitlePickerWidget = {
    val radioButtons = new TitlePickerWidget(idStr).radioButtons
    radioButtons.find(_.underlying.getAttribute("id") endsWith title)
      .fold(throw new Exception) ( _.isSelected should equal(true))
    radioButtons.filterNot(_.underlying.getAttribute("id") endsWith title)
      .map(_.isSelected should equal(false))
    this
  }

  def assertNothingSelected(): TitlePickerWidget = {
    radioButtons.foreach(_.isSelected should equal(false))
    this
  }

  lazy val otherText = textField(id(s"${idStr}_titleText"))
}
