package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers
import uk.gov.dvla.vehicles.presentation.common.models

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory, _}
import models.DateModel.Form.{OptionalDateId, DateId}
import org.openqa.selenium.WebDriver

class DatePage(implicit driver: WebDriver) extends Page with WebBrowserDSL {

  final val address = "/date"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Date Of Birth Input"

  lazy val optional: DateWidget = DateWidget(OptionalDateId)

  lazy val required: DateWidget = DateWidget(DateId)

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

object DatePage {
  def instance(implicit driver: WebDriver) = new DatePage
}


class DateWidget(idStr: String)(implicit driver: WebDriver) extends WebBrowserDSL {
  def label: String = ???

  def hint: String = ???

  lazy val day: TelField = telField(id(s"${idStr}_day"))

  lazy val month: TelField = telField(id(s"${idStr}_month"))

  lazy val year: TelField = telField(id(s"${idStr}_year"))
}

object DateWidget {
  def apply(idStr: String)(implicit driver: WebDriver): DateWidget = new DateWidget(idStr)
}
