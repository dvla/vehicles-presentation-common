package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.pageTitle
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{FeedbackFormPage, ErrorPanel}

class FeedbackFormIntegrationSpec extends UiSpec with TestHarness {

  "Feedback Form integration" should {
    "be presented" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      pageTitle should equal(FeedbackFormPage.title)
    }

    "redirects to the next page given valid input" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate()
      pageTitle should equal("Success")
    }

    "reject submit when field is blank" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "reject submit when field contains less than minimum characters" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when field contains more than maximum characters" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "1" * 1201)
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
