package composition

import com.google.inject.Guice

trait Composition {
  lazy val injector = Guice.createInjector(DevModule)
}
