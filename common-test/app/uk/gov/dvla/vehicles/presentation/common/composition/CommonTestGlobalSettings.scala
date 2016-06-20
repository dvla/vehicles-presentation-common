package uk.gov.dvla.vehicles.presentation.common.composition

import play.api.{Application, GlobalSettings, Logger}

trait CommonTestGlobalSettings extends GlobalSettings with Composition {

  val serviceName = "vehicles-presentation-common-test"

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  override def onStart(app: Application) {
    Logger.info(s"$serviceName started")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info(s"$serviceName stopped")
  }
}
