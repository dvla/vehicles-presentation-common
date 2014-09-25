package pages

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory, _}
import models.DateOfSaleModel.Form.{OptionalDateId, DateId}
import org.openqa.selenium.WebDriver

class DateOfSalePage(implicit driver: WebDriver) extends Page with WebBrowserDSL {

  final val address = "/date-of-sale"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Date Of Sale Input"

  lazy val optional: DateOfSaleWidget = DateOfSaleWidget(OptionalDateId)

  lazy val required: DateOfSaleWidget = DateOfSaleWidget(DateId)

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(day: String = "3", month: String = "4", year: String = "2014",
               day1: String = "30", month1: String = "12", year1: String = "1970")
              (implicit driver: WebDriver) {
    go to this

    optional.day.value = day
    optional.month.value = month
    optional.year.value = year

    required.day.value = day1
    required.month.value = month1
    required.year.value = year1

    click on submit
  }
}

object DateOfSalePage {
  def instance(implicit driver: WebDriver) = new DateOfSalePage
}


class DateOfSaleWidget(idStr: String)(implicit driver: WebDriver) extends WebBrowserDSL {
  def label: String = ???

  def hint: String = ???

  lazy val day: TelField = telField(id(s"${idStr}_day"))

  lazy val month: TelField = telField(id(s"${idStr}_month"))

  lazy val year: TelField = telField(id(s"${idStr}_year"))
}

object DateOfSaleWidget {
  def apply(idStr: String)(implicit driver: WebDriver): DateOfSaleWidget = new DateOfSaleWidget(idStr)
}