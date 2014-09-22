package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory, _}
import models.DateModel.Form.DateId
import org.openqa.selenium.WebDriver

class NonFutureDatePage(implicit driver: WebDriver) extends Page with WebBrowserDSL {

  final val address = "/non-future-date"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Non Future Date Input"

  //lazy val optional: DateOfBirthWidget = DateOfBirthWidget(DateOfBirthDayId)

  lazy val required: DateOfBirthWidget = DateOfBirthWidget(DateId)

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(day: String = "3", month: String = "4", year: String = "2014")
              (implicit driver: WebDriver) {
    go to this

    required.day.value = day
    required.month.value = month
    required.year.value = year

    click on submit
  }
}

object NonFutureDatePage {
  def instance(implicit driver: WebDriver) = new NonFutureDatePage
}


class NonFutureDateWidget(idStr: String)(implicit driver: WebDriver) extends WebBrowserDSL {
  def label: String = ???

  def hint: String = ???

  lazy val day: TelField = telField(id(s"${idStr}_day"))

  lazy val month: TelField = telField(id(s"${idStr}_month"))

  lazy val year: TelField = telField(id(s"${idStr}_year"))
}

object NonFutureDateWidget {
  def apply(idStr: String)(implicit driver: WebDriver): NonFutureDateWidget = new NonFutureDateWidget(idStr)
}