package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers

import helpers.webbrowser.WebBrowserDSL
import org.openqa.selenium.{SearchContext, By, WebDriver}

object ErrorPanel extends WebBrowserDSL {
  def numberOfErrors(implicit driver: SearchContext): Int =
    driver.findElement(By.cssSelector(".validation-summary")).findElements(By.tagName("li")).size

  def text(implicit driver: SearchContext): String =
    driver.findElement(By.cssSelector(".validation-summary")).getText

  def hasErrors(implicit driver: SearchContext): Boolean = find(id("validation-summary")).isDefined
}
