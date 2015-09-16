package uk.gov.dvla.vehicles.presentation.common.services

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.services.SEND.Contents
import uk.gov.dvla.vehicles.presentation.common.{WithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm

class FeedbackMessageBuilderSpec extends UnitSpec {

  val trackingId = TrackingId("test-tracking-id")
  val name = "test-name"
  val email = "test@test.com"

  "Message builder" should {
    "include contain correct details" in new WithApplication {
      val feedbackForm = FeedbackForm(feedback = "Feedback text", name = Some(name), email = Some(email))
      val message: Contents = FeedbackMessageBuilder.buildWith(feedbackForm, trackingId)
      message.htmlMessage should include(s"<p>trackingId : $trackingId</p>")
      message.htmlMessage should include(s"received from: $name")
      message.htmlMessage should include(s"email: $email")
      message.plainMessage should include(s"trackingId : $trackingId")
      message.plainMessage should include(s"received from: $name")
      message.plainMessage should include(s"email: $email")
    }

    "handles no name or email address" in new WithApplication() {
      val feedbackForm = FeedbackForm(feedback = "Feedback text", name = None, email = None)
      val message: Contents = FeedbackMessageBuilder.buildWith(feedbackForm, trackingId)
      message.htmlMessage should include("received from: 'no name given'")
      message.htmlMessage should include("email: 'no email given'")
      message.plainMessage should include("received from: 'no name given'")
      message.plainMessage should include("email: 'no email given'")
    }

    "correctly converts reserved characters" in new WithApplication() {
      val feedbackForm = FeedbackForm(feedback = "Feedback text: <>", name = None, email = None)
      val message: Contents = FeedbackMessageBuilder.buildWith(feedbackForm, trackingId)
      message.htmlMessage should include("&lt;&gt;")
    }
  }
}
