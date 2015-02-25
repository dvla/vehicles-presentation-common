package uk.gov.dvla.vehicles.presentation.common.model

case class CacheKeyPrefix(prefix: String) {
  override def toString() = prefix
}
