package uk.gov.dvla.vehicles.presentation.common.pages

import uk.gov.dvla.vehicles.presentation.common.helpers

import helpers.webbrowser.{Element, Page, WebBrowserDSL, TextArea, WebDriverFactory}
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm.Form.feedback
import org.openqa.selenium.WebDriver

object FeedbackFormPage extends Page with WebBrowserDSL {

  final val address = "/feedback-form"
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Feedback Form"

  def feedbackElement(implicit driver: WebDriver): TextArea = textArea(id(feedback))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (feedback: String = "f" * 100)(implicit driver: WebDriver) {
    go to FeedbackFormPage
    feedbackElement enter feedback
    click on submit
  }
}
