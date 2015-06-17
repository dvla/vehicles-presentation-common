package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebBrowserDSL, WebDriverFactory}

object VersionPage extends Page with WebBrowserDSL {

  final val address = "/version"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Version"
}
