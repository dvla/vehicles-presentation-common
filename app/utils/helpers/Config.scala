package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getProperty, getDurationProperty}
import scala.concurrent.duration.DurationInt

class Config {
  // Micro-service config
  val vehicleLookupMicroServiceBaseUrl: String = getProperty("vehicleLookup.baseUrl", "NOT FOUND")

  // Ordnance survey config
  val ordnanceSurveyMicroServiceUrl: String = getProperty("ordnancesurvey.baseUrl", "NOT FOUND")
  val ordnanceSurveyRequestTimeout: Int = getProperty("ordnancesurvey.requestTimeout", 5.seconds.toMillis.toInt)

  // GDS address lookup config
  val gdsAddressLookupBaseUrl: String = getProperty("gdsaddresslookup.baseUrl", "")
  val gdsAddressLookupAuthorisation: String = getProperty("gdsaddresslookup.authorisation", "")
  val gdsAddressLookupRequestTimeout: Int = getProperty("gdsaddresslookup.requestTimeout", 5.seconds.toMillis.toInt)

  // Dispose
  val disposeVehicleMicroServiceBaseUrl: String = getProperty("disposeVehicle.baseUrl", "NOT FOUND")
  val disposeMsRequestTimeout: Int = getProperty("disposeVehicle.requestTimeout", 5.seconds.toMillis.toInt)

  // Brute force prevention config
  val bruteForcePreventionMicroServiceBaseUrl: String = getProperty("bruteForcePrevention.baseUrl", "NOT FOUND")
  val bruteForcePreventionTimeout: Int = getProperty("bruteForcePrevention.requestTimeout", 5.seconds.toMillis.toInt)
  val isBruteForcePreventionEnabled: Boolean = getProperty("bruteForcePrevention.enabled", default = true)
  val bruteForcePreventionServiceNameHeader: String = getProperty("bruteForcePrevention.headers.serviceName", "")
  val bruteForcePreventionMaxAttemptsHeader: Int = getProperty("bruteForcePrevention.headers.maxAttempts", 3)
  val bruteForcePreventionExpiryHeader: String = getProperty("bruteForcePrevention.headers.expiry", "")

  // Prototype message in html
  val isPrototypeBannerVisible: Boolean = getProperty("prototype.disclaimer", default = true)

  // Prototype survey URL
  val prototypeSurveyUrl: String = getProperty("survey.url", "")
  val prototypeSurveyPrepositionInterval: Long = getDurationProperty("survey.interval", 7.days.toMillis)

  // Google analytics
  val isGoogleAnalyticsEnabled: Boolean = getProperty("googleAnalytics.enabled", default = true)

  // Progress step indicator
  val isProgressBarEnabled: Boolean = getProperty("progressBar.enabled", default = true)

  val isHtml5ValidationEnabled: Boolean = getProperty("html5Validation.enabled", default = false)

  val startUrl: String = getProperty("start.page", default = "NOT FOUND")
}