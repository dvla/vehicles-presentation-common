package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

// Needed by the picocontainer

import org.openqa.selenium.support.events.EventFiringWebDriver

class WebBrowserDriver extends EventFiringWebDriver(WebDriverFactory.webDriver)

class WebBrowserFirefoxDriver extends EventFiringWebDriver(WebDriverFactory.webDriver(targetBrowser = "firefox", javascriptEnabled = true))