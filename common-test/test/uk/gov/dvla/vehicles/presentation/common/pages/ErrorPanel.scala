package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.{NoSuchElementException, SearchContext, By}
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.find
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory

object ErrorPanel {

  implicit lazy val webDriver = WebDriverFactory.webDriver

  def numberOfErrors(implicit driver: SearchContext): Int =
    driver.findElement(By.cssSelector(".validation-summary")).findElements(By.tagName("li")).size

  def text(implicit driver: SearchContext): String = {
    val element = try Some(driver.findElement(By.cssSelector(".validation-summary")))
    catch {
      case e: NoSuchElementException => None
      case e: Throwable => throw e
    }
    element.map(_.getText).getOrElse("")
  }

  def hasErrors(implicit driver: SearchContext): Boolean = find(id("validation-summary")).isDefined
}
