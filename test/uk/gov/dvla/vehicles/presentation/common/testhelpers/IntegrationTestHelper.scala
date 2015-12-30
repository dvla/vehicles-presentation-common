package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Seconds
import org.scalatest.time.Span

trait IntegrationTestHelper extends UnitTestHelper {
  override protected val timeout = Timeout(scaled(Span(5, Seconds)))
}
