package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

// Needed by the picocontainer

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.scalatest.AppendedClues

class WebBrowserDriver extends EventFiringWebDriver(WebDriverFactory.webDriver)

class WebBrowserFirefoxDriver extends EventFiringWebDriver(WebDriverFactory.webDriver(targetBrowser = "firefox", javascriptEnabled = true))

trait WithClue extends AppendedClues {

  def trackingId(implicit webDriver: WebDriver) =
    "- trackingId: " + webDriver.manage().getCookieNamed("tracking_id").getValue
}