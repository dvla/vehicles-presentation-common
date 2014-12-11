package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

import org.openqa.selenium.support.events.EventFiringWebDriver

class WebBrowserDriverWithJavaScript extends EventFiringWebDriver(WebDriverFactory.webDriver("htmlunit", javascriptEnabled = true))
