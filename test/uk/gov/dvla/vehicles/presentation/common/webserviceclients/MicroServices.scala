package uk.gov.dvla.vehicles.presentation.common.webserviceclients

object MicroServices {
  // The base urls are now mandatory so they must be set to something to enable the test application to start
  // However, only the address lookup base url needs to be provided. Brute force and vehicle and keeper lookup
  // are not required because they are not bound in uk.gov.dvla.vehicles.presentation.common.composition.DevModule.
  // The Address Picker widget is injected with the AddressLookupService.
  final val DefaultBaseUrls = Map("ordnancesurvey.baseUrl" -> "")
}