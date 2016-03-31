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

    "reject submit when feedback field is blank" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when feedback field contains less than minimum characters" in new WebBrowserForSelenium {
      FeedbackFormPage.navigate(feedback = "1")
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

    "allow submit when 'No' webchat option selected" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "1" * 10
      click on FeedbackFormPage.webChatOptionInvisibleElement
      click on FeedbackFormPage.submit
      pageTitle should equal("Success")
    }

    "reject submit when 'Yes' webchat option selected and no feedback text given" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "1" * 10
      click on FeedbackFormPage.webChatOptionVisibleElement
      click on FeedbackFormPage.submit
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when 'Yes' webchat option selected and only 1 character entered" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "1" * 10
      click on FeedbackFormPage.webChatOptionVisibleElement
      FeedbackFormPage.webChatElement.value ="1"
      click on FeedbackFormPage.submit
      ErrorPanel.numberOfErrors should equal(1)
    }

    "reject submit when 'Yes' webchat option selected and more than maximum characters entered" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "1" * 10
      click on FeedbackFormPage.webChatOptionVisibleElement
      FeedbackFormPage.webChatElement.value = "1" * 501
      click on FeedbackFormPage.submit
      ErrorPanel.numberOfErrors should equal(1)
    }

    "allow submit when 'Yes' webchat option selected and valid number of characters entered" in new WebBrowserForSelenium {
      go to FeedbackFormPage
      FeedbackFormPage.feedbackElement.value = "1" * 10
      click on FeedbackFormPage.webChatOptionVisibleElement
      FeedbackFormPage.webChatElement.value = "1" * 20
      click on FeedbackFormPage.submit
      pageTitle should equal("Success")
    }

    "show a count down of webchat characters remaining as user types in text" in new WebBrowserWithJs {
      go to FeedbackFormPage

      FeedbackFormPage.waitUntilJavascriptReady

      click on FeedbackFormPage.webChatOptionVisibleElement

      FeedbackFormPage.webChatElement.value = ""
      FeedbackFormPage.webChatCounterElement.text should equal("500")

      FeedbackFormPage.webChatElement.underlying.sendKeys("12345")
      FeedbackFormPage.webChatCounterElement.text should equal("495")
    }
  }
}
