package uk.gov.dvla.vehicles.presentation.common

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Span}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar

abstract class UnitSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures with TestComposition {
  protected val timeout = Timeout(Span(1, Second))
}