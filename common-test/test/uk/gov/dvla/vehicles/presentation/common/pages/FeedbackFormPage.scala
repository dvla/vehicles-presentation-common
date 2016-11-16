package uk.gov.dvla.vehicles.presentation.common.pages

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.radioButton
import org.scalatest.selenium.WebBrowser.RadioButton
import org.scalatest.selenium.WebBrowser.textArea
import org.scalatest.selenium.WebBrowser.TextArea
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm.Form.{feedback, rating}

object FeedbackFormPage extends Page {

  final val address = "/feedback-form"
  override val url: String = WebDriverFactory.testUrl + address
  final override val title: String = "Feedback Form"

  def feedbackElement(implicit driver: WebDriver): TextArea = textArea(id(feedback))

  def rating_x(rate: String)(implicit driver: WebDriver) = radioButton(id(s"${rating}_${rate}"))

  def submit(implicit driver: WebDriver): Element = find(id("submit")).get

  def navigate (rate: String = "3", feedback: String = "f" * 100)(implicit driver: WebDriver) {
    go to FeedbackFormPage
    feedbackElement.value = feedback
    click on rating_x(rate)
    click on submit
  }

  def feedbackCounterElement(implicit driver: WebDriver): Element =
    find(id("feedback-character-countdown"))
      .getOrElse(throw new Exception("Unable to find feedback character counter "))
}
