package uk.gov.dvla.vehicles.presentation.common.testhelpers

import play.api.GlobalSettings
import play.api.test.FakeApplication

/**
 * factory for creating fake applications
 */
object LightFakeApplication {

  def apply(global: GlobalSettings, additionalConfiguration: Map[String, _ <: Any] = Map.empty) = FakeApplication(
    withGlobal = Some(global),
    additionalPlugins = Seq("uk.gov.dvla.vehicles.presentation.common.testhelpers.CachedMessagesPlugin"),
    withoutPlugins = Seq("play.api.i18n.DefaultMessagesPlugin"),
    additionalConfiguration = additionalConfiguration
  )

}