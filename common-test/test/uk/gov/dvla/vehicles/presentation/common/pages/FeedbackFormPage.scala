package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.textArea
import org.scalatest.selenium.WebBrowser.TextArea
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
}
