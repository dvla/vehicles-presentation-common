package uk.gov.dvla.vehicles.presentation.common.testhelpers

import play.api.GlobalSettings
import play.api.test.FakeApplication

/**
 * factory for creating fake applications
 */
object LightFakeApplication {
  def create(global: GlobalSettings) = FakeApplication(
    withGlobal = Some(global),
    additionalPlugins = Seq("uk.gov.dvla.vehicles.presentation.common.testhelpers.CachedMessagesPlugin"),
    withoutPlugins = Seq("play.api.i18n.DefaultMessagesPlugin")
  )

  def create(global: GlobalSettings, additionalConfiguration: Map[String, _]) = FakeApplication(
    withGlobal = Some(global),
    additionalPlugins = Seq("uk.gov.dvla.vehicles.presentation.common.testhelpers.CachedMessagesPlugin"),
    withoutPlugins = Seq("play.api.i18n.DefaultMessagesPlugin"),
    additionalConfiguration = additionalConfiguration
  )

}