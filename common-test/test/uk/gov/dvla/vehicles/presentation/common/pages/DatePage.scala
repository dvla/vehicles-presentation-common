package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{WebDriver}
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.cssSelector
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.{telField, TelField}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.DateModel.Form.{DateId, OptionalDateId}

class DatePage(implicit driver: WebDriver) extends Page {

  final val address = "/valtech-date"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Date Input"

  lazy val optional: DateWidget = DateWidget(OptionalDateId)

  lazy val required: DateWidget = DateWidget(DateId)

  def submit(implicit driver: WebDriver): Element = {
    find(id("submit")).get
  }

  //Q. why does optional have defaults?
  def navigate(day: String = "01", month: String = "01", year: String = "1970",
               day1: String = "01", month1: String = "01", year1: String = "1970")
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

  def useTodaysDateButton(implicit driver: WebDriver): Element = {
    // This is the element / dom node that must be clicked to show / hide content
    find(cssSelector("#todays_date")) getOrElse(throw new Exception("Unable to find help icon "))
  }
}

class DateWidget(idStr: String)(implicit driver: WebDriver) {
//  implicit lazy val webDriver = WebDriverFactory.webDriver

  def label: String = ???

  def hint: String = ???

  lazy val day: TelField = telField(id(s"${idStr}_day"))

  lazy val month: TelField = telField(id(s"${idStr}_month"))

  lazy val year: TelField = telField(id(s"${idStr}_year"))

  def isDisplayed: Boolean = day.isDisplayed && month.isDisplayed && year.isDisplayed
}

object DateWidget {
  def apply(idStr: String)(implicit driver: WebDriver): DateWidget = new DateWidget(idStr)
}
