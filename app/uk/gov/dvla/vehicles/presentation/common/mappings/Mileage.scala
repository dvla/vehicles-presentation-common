package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.{number, optional}
import play.api.data.Mapping

object Mileage {
  private final val MinLength = 1
  final val MaxLength = 6
  final val Max = 999999 // confirmed as max size by BAs
  final val Pattern = s"\\d{$MinLength,$MaxLength}" // Digits only with specified size.

  def mileage: Mapping[Option[Int]] = optional(number(min = 0, max = Max))
}