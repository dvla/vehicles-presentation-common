package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common.services.{FeedbackMessageBuilder, SEND}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration

/**
 * Feedback base controller.
 * Provides the sendFeedback method, that creates and send a feedback message.
 * All Feedback Controllers should extends this method and call sendFeedback on submit.
 */
trait FeedbackBase extends Controller {

  val emailConfiguration: EmailConfiguration

  def sendFeedback(contents: String, subject: String): Unit = {

    import scala.language.postfixOps

    import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

    implicit val implicitEmailConf = implicitly[EmailConfiguration](emailConfiguration)

    //check if there are multiple emails for feedback
    val feedbackEmail: Array[String] = emailConfiguration.feedbackEmail.email.split(",")

    val template: Contents = FeedbackMessageBuilder.buildWith(contents)

    SEND email template withSubject subject to feedbackEmail.toList send
  }
}
