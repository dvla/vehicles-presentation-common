package uk.gov.dvla.vehicles.presentation.common.views.helpers

import scala.util.Random

object Space {
   def * = " " * new Random().nextInt(10)
}