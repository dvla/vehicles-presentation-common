package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.singleSel
import org.scalatest.selenium.WebBrowser.SingleSel
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.find
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.models.ValtechInputDayMonthYearModel.Form.DateOfBirthId

object ValtechInputDayMonthYearPage extends Page {

  final val address = "/valtech-input-day-month-year"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Valtech input day month year"

  val dayDateOfBirthId = "dateOfBirth_day"
  val monthDateOfBirthId = "dateOfBirth_month"
  val yearDateOfBirthId = "dateOfBirth_year"

  def dateOfBirthDay(implicit driver: WebDriver): SingleSel = singleSel(id(s"${DateOfBirthId}_day"))

  def dateOfBirthMonth(implicit driver: WebDriver): SingleSel = singleSel(id(s"${DateOfBirthId}_month"))

  def dateOfBirthYear(implicit driver: WebDriver): SingleSel = singleSel(id(s"${DateOfBirthId}_year"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate(day: String = "3", month: String = "4", year: String = "2014")
              (implicit driver: WebDriver) {
    go to ValtechInputDayMonthYearPage

    dateOfBirthDay.value = day
    dateOfBirthMonth.value = month
    dateOfBirthYear.value = year

    click on submit
  }
}
