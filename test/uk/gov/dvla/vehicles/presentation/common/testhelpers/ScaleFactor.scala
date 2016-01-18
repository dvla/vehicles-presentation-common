package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Second
import org.scalatest.time.Span
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS

trait ScaleFactor extends ScalaFutures {
  // The default PatienceConfig from ScalaTest uses scaled time spans.
  // The interval and timeout for the PatienceConfig will be scaled,
  // allowing us to run the tests with fail-fast values for local testing
  // or high values for testing on Continuous Integration servers (which tend to be much slower).
  // http://doc.scalatest.org/2.2.1/index.html#org.scalatest.concurrent.ScaledTimeSpans
  // We will read spanScaleFactor from system properties (if one exists, otherwise use the default).
  override def spanScaleFactor = sys.props.getOrElse("spanScaleFactor", "1.0").toDouble
}
