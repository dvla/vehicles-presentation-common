package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.data.{Form, FormError}
import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.TrackingId
import common.model.FeedbackForm
import common.model.FeedbackForm.Form.feedback
import common.services.{DateService, FeedbackMessageBuilder, SEND}
import common.services.SEND.EmailConfiguration
import common.webserviceclients.emailservice.EmailService
import common.webserviceclients.healthstats.HealthStats
import common.views.helpers.FormExtensions.formBinding

/**
 * Feedback base controller.
 * Provides the sendFeedback method, that creates and send a feedback message.
 * All Feedback Controllers should extends this method and call sendFeedback on submit.
 */
trait FeedbackBase extends Controller {

  val emailConfiguration: EmailConfiguration
  val emailService: EmailService
  val dateService: DateService
  val healthStats: HealthStats

  val form = Form(
    FeedbackForm.Form.Mapping
  )

  def sendFeedback(feedback: FeedbackForm, subject: String, trackingId: TrackingId): Unit = {

    import scala.language.postfixOps
    import SEND.Contents // Keep this local so that we don't pollute rest of the class with unnecessary imports.

    implicit val implicitEmailConf = implicitly[EmailConfiguration](emailConfiguration)
    implicit val implicitEmailService = implicitly[EmailService](emailService)
    implicit val implicitDateService = implicitly[DateService](dateService)
    implicit val implicitHealthStats = implicitly[HealthStats](healthStats)

    //check if there are multiple emails for feedback
    val feedbackEmail: Array[String] = emailConfiguration.feedbackEmail.email.split(",")

    val template: Contents = FeedbackMessageBuilder.buildWith(feedback, trackingId)

    SEND email template withSubject subject to feedbackEmail.toList send trackingId
  }
}
