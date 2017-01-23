package uk.gov.dvla.vehicles.presentation.common.testhelpers

object ApplicationContext {
  type ApplicationRoot = String

  def buildAppUrl(urlPart: String)(implicit applicationRoot: ApplicationRoot) = {
    val appContextWithSlash = if (!applicationRoot.endsWith("/")) s"$applicationRoot/" else applicationRoot
    val urlPartWithoutSlash = urlPart.dropWhile(_ == '/')

    appContextWithSlash + urlPartWithoutSlash
  }
}
