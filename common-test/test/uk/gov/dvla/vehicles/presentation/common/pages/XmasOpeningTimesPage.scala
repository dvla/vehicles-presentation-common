package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{WebElement, By, SearchContext}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.services.DateService

class XmasOpeningTimesPage(implicit driver: SearchContext, fakeDateService: DateService) extends Page {

  val dateService = fakeDateService
  final val address = "/xmas-opening-times"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "View Christmas opening times"

  private val xmasOpeningTimesLink = driver.findElements(By.id("xmas-opening-times"))
  lazy val link: WebElement = xmasOpeningTimesLink.get(0)
}

object XmasOpeningTimesPage {
  def instance(implicit driver: SearchContext, fakeDateService: DateService) = new XmasOpeningTimesPage
}