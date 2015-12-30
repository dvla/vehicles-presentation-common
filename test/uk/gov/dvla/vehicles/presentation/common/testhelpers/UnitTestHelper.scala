package uk.gov.dvla.vehicles.presentation.common.testhelpers

import org.scalatest.concurrent.Eventually
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import org.scalatest.WordSpec

trait UnitTestHelper
  extends WordSpec
  with Eventually
  with Matchers
  with MockitoSugar
  with ScaleFactor
