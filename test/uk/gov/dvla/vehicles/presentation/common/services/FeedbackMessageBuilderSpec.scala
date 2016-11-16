package uk.gov.dvla.vehicles.presentation.common.services

import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.services.SEND.Contents
import uk.gov.dvla.vehicles.presentation.common.{TestWithApplication, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm

class FeedbackMessageBuilderSpec extends UnitSpec {

  val trackingId = TrackingId("test-tracking-id")

  "Message builder" should {
    "contain correct details" in new TestWithApplication {
      val feedbackForm = FeedbackForm(rating = "3", feedback = "Feedback text")
      val message: Contents = FeedbackMessageBuilder.buildWith(feedbackForm, trackingId)
      message.htmlMessage should include(s"<p>trackingId : $trackingId</p>")
      message.plainMessage should include(s"trackingId : $trackingId")
    }

    "correctly convert reserved characters" in new TestWithApplication() {
      val feedbackForm = FeedbackForm(rating = "5", feedback = "Feedback text: <>")
      val message: Contents = FeedbackMessageBuilder.buildWith(feedbackForm, trackingId)
      message.htmlMessage should include("&lt;&gt;")
    }
  }
}
