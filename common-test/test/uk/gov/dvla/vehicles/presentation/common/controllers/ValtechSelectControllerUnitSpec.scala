package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.TestWithApplication
import uk.gov.dvla.vehicles.presentation.common.helpers.{CookieFactoryForUnitSpecs, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.models.ValtechSelectModel.Form.{FirstOption, SecondOption}

class ValtechSelectControllerUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new TestWithApplication {
      whenReady(present) {
        r =>
          r.header.status should equal(OK)
      }
    }

    "not display drop down pre-selected when nothing has been previously selected" in new TestWithApplication {
      val request = FakeRequest()
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should not include "selected>"
    }

    "display drop down pre-selected when cookie contains first option" in new TestWithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.valtechSelect(selectedOption = FirstOption))
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should include(expectedOptionSelected(FirstOption))
    }

    "display drop down pre-selected when cookie contains second option" in new TestWithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.valtechSelect(selectedOption = SecondOption))
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should include(expectedOptionSelected(SecondOption))
    }
  }

  private def expectedOptionSelected(option: String) = s"""<option value="$option" selected>"""

  private def valtechSelectController = injector.getInstance(classOf[ValtechSelectController])

  private lazy val present = {
    val request = FakeRequest()
    valtechSelectController.present(request)
  }
}
