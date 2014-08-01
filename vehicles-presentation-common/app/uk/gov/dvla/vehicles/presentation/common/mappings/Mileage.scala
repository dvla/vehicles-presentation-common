package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.{number, optional}
import play.api.data.Mapping

object Mileage {
  private final val minLength = 1
  final val maxLength = 6
  final val Max = 999999 // confirmed as max size by BAs
  final val Pattern = s"\\d{$minLength,$maxLength}" // Digits only with specified size.

  def mileage (max: Int = Max): Mapping[Option[Int]] = optional(number(max = max))
}