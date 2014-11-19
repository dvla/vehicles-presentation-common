package pages

import helpers.webbrowser.{WebDriverFactory, WebBrowserDSL, Page}

object VersionPage extends Page with WebBrowserDSL {

  final val address = "/version"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Version"
}
