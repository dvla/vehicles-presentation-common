package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common.models
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.{CookieFactoryForUnitSpecs, UnitSpec, WithApplication}
import play.api.test.FakeRequest
import play.api.test.Helpers.{OK, contentAsString, defaultAwaitTimeout}
import models.ValtechSelectModel.Form.{FirstOption, SecondOption}

final class ValtechSelectControllerUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithApplication {
      whenReady(present) {
        r =>
          r.header.status should equal(OK)
      }
    }

    "not display drop down pre-selected when nothing has been previously selected" in new WithApplication {
      val request = FakeRequest()
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should not include("selected>")
    }

    "display drop down pre-selected when cookie contains first option" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.valtechSelect(selectedOption = FirstOption))
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should include(expectedOptionSelected(FirstOption))
    }

    "display drop down pre-selected when cookie contains second option" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.valtechSelect(selectedOption = SecondOption))
      val result = valtechSelectController.present(request)
      val content = contentAsString(result)
      content should include(expectedOptionSelected(SecondOption))
    }
  }

  private def expectedOptionSelected(option: String) = s"""<option value="$option" selected>"""

  private val valtechSelectController = injector.getInstance(classOf[ValtechSelectController])

  private lazy val present = {
    val request = FakeRequest()
    valtechSelectController.present(request)
  }
}
