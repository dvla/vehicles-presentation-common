package pages

import helpers.webbrowser._
import org.openqa.selenium.WebDriver
import models.ValtechInputDayMonthYearModel.Form.DateOfBirthId

object ValtechInputDayMonthYearPage extends Page with WebBrowserDSL {

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

    dateOfBirthDay select day
    dateOfBirthMonth select month
    dateOfBirthYear select year

    click on submit
  }
}
