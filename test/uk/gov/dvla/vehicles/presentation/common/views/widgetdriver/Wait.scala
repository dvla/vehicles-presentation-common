package uk.gov.dvla.vehicles.presentation.common.views.widgetdriver

import org.openqa.selenium.{StaleElementReferenceException, WebDriver, By}
import org.openqa.selenium.support.ui.{Select, WebDriverWait, ExpectedCondition}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDSL
import scala.collection.JavaConversions._

object Wait extends WebBrowserDSL {
  def until[T](condition: ExpectedCondition[T], timeoutSec: Int = 3)(implicit driver: WebDriver): T =
    (new WebDriverWait(driver, timeoutSec)).until(condition)

  def elementHasAnyText (locator: By): ExpectedCondition[Boolean] = {
    new ExpectedCondition[Boolean]() {
      override def apply(driver: WebDriver): Boolean =
        try driver.findElement(locator).getText.isEmpty
        catch {case e: Throwable => false}

      override def toString: String = String.format("text ('%s') to be present in element found by %s", locator)
    }
  }

  def windowSizeGraterThen(x: Int, y: Int): ExpectedCondition[Boolean] = {
    new ExpectedCondition[Boolean]() {
      override def apply(driver: WebDriver): Boolean = {
        val size = driver.manage().window().getSize
        if (size.getWidth < x || size.getHeight < y) false
        else true
      }
    }
  }
}
