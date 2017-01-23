package uk.gov.dvla.vehicles.presentation.common

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Second, Span}
import testhelpers.UnitTestHelper

abstract class UnitSpec extends UnitTestHelper {
  protected val timeout = Timeout(Span(1, Second))
}
