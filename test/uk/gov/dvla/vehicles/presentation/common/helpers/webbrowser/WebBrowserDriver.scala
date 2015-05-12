package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

// Needed by the picocontainer

import java.util

import org.openqa.selenium.WebDriver.{Navigation, TargetLocator, Options}
import org.openqa.selenium.{By, WebElement, WebDriver}
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.scalatest.AppendedClues

class WebBrowserDriver extends EventFiringWebDriver(WebDriverFactory.webDriver)

class PhantomJsDefaultDriver extends WebDriver {
  private val driver = WebDriverFactory.defaultBrowserPhantomJs
  override def getPageSource: String = driver.getPageSource
  override def findElements(by: By): util.List[WebElement] = driver.findElements(by)
  override def getWindowHandle: String = driver.getWindowHandle
  override def get(url: String): Unit = driver.get(url)
  override def manage(): Options = driver.manage()
  override def getWindowHandles: util.Set[String] = driver.getWindowHandles
  override def switchTo(): TargetLocator = driver.switchTo()
  override def close(): Unit = driver.close()
  override def quit(): Unit = driver.quit()
  override def getCurrentUrl: String = driver.getCurrentUrl
  override def navigate(): Navigation = driver.navigate()
  override def getTitle: String = driver.getTitle
  override def findElement(by: By): WebElement = driver.findElement(by)
}

class WebBrowserFirefoxDriver extends EventFiringWebDriver(WebDriverFactory.webDriver(targetBrowser = "firefox", javascriptEnabled = true))

trait WithClue extends AppendedClues {

  def trackingId(implicit webDriver: WebDriver) =
    "- trackingId: " + webDriver.manage().getCookieNamed("tracking_id").getValue
}