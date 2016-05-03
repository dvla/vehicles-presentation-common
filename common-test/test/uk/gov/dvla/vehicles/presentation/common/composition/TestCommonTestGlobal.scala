package uk.gov.dvla.vehicles.presentation.common.composition

import play.api.GlobalSettings
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.GlobalCreator

object TestCommonTestGlobal extends CommonTestGlobal with TestComposition

trait TestGlobalCreator extends GlobalCreator {
  override def global: GlobalSettings = TestCommonTestGlobal
}
