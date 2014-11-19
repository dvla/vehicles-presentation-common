package uk.gov.dvla.vehicles.presentation.common.composition

import com.google.inject.Guice

trait Composition {
  lazy val injector = Guice.createInjector(DevModule)
}
