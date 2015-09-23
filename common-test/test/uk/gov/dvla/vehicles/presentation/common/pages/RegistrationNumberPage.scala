package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{WebElement, By, SearchContext}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory

class RegistrationNumberPage(implicit driver: SearchContext) extends Page {

  final val address = "/registration-number"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Registration Number"

  private val regNumber = driver.findElements(By.className("reg-number"))
  lazy val regNumber1: WebElement = regNumber.get(0)
}

object RegistrationNumberPage {
  def instance(implicit driver: SearchContext) = new RegistrationNumberPage
}
