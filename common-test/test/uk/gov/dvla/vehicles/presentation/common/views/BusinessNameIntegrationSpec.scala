package uk.gov.dvla.vehicles.presentation.common.views

import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{BusinessNamePage, ErrorPanel}

class BusinessNameIntegrationSpec extends UiSpec with TestHarness {

  "BusinessName integration" should {
    "be presented" in new WebBrowser {
      go to BusinessNamePage
      page.title should equal(BusinessNamePage.title)
    }

    "redirects to the next page given valid input" in new WebBrowser {
      BusinessNamePage.navigate()
      page.title should equal("Success")
    }

    "reject submit when field is blank" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "")
      ErrorPanel.numberOfErrors should equal(3)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "A")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when business name fails regular expression because it contains @ character" in new WebBrowser {
      BusinessNamePage.navigate(businessName = "Foo @ Bar")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}

