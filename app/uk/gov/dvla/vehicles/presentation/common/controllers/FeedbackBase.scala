package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm
import uk.gov.dvla.vehicles.presentation.common.services.{DateService, FeedbackMessageBuilder, SEND}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats

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
