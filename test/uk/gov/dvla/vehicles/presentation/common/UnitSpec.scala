package uk.gov.dvla.vehicles.presentation.common

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.time.{Second, Span}

abstract class UnitSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures {
  protected val timeout = Timeout(Span(1, Second))
}