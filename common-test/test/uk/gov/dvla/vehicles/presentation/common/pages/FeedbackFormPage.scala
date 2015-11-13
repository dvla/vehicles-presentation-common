package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm.Form.feedback

object FeedbackFormPage extends Page {

  final val address = "/feedback-form"
  override val url: String = WebDriverFactory.testUrl + address
  final override val title: String = "Feedback Form"

  def feedbackElement(implicit driver: WebDriver): TextArea = textArea(id(feedback))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (feedback: String = "f" * 100)(implicit driver: WebDriver) {
    go to FeedbackFormPage
    feedbackElement.value = feedback
    click on submit
  }

  def feedbackCounterElement(implicit driver: WebDriver): Element =
    find(cssSelector(".character-countdown")) getOrElse(throw new Exception("Unable to find character counter "))
}
