package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.selenium.WebBrowser.click
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

    "redirect to the next page given valid input" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate()
      pageTitle should equal("Success")
    }

    "redirect to the next page when feedback field is blank" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "")
      pageTitle should equal("Success")
    }

    "reject submit when no rating given" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "f" * 10
      click on FeedbackFormPage.submit
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when feedback field contains more than maximum characters" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "1" * 1201)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "show a count down of feedback characters remaining as user types in text" in new WebBrowserWithJs {
      go to FeedbackFormPage

      FeedbackFormPage.waitUntilJavascriptReady

      FeedbackFormPage.feedbackElement.value = ""
      FeedbackFormPage.feedbackCounterElement.text should equal("500")

      FeedbackFormPage.feedbackElement.underlying.sendKeys("1234567890")
      FeedbackFormPage.feedbackCounterElement.text should equal("490")
    }
  }
}
