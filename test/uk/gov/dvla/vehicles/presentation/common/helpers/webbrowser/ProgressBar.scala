package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

import play.api.test.FakeApplication
import uk.gov.dvla.vehicles.presentation.common.testhelpers.LightFakeApplication

trait ProgressBar {
  self: GlobalCreator =>

  val fakeApplicationWithProgressBarFalse = LightFakeApplication(global, Map("progressBar.enabled" -> "false"))

  val fakeApplicationWithProgressBarTrue = LightFakeApplication(global, Map("progressBar.enabled" -> "true"))

}

object ProgressBar {
  def progressStep(currentStep: Int): String = {
    val end = 9
    s"Step $currentStep of $end"
  }

  final val div: String = """<div class="progress-indicator">"""
}
