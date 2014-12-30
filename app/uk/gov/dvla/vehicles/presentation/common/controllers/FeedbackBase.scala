package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.Controller
import uk.gov.dvla.vehicles.presentation.common.services.{FeedbackMessageBuilder, SEND}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration

/**
 * Created by gerasimosarvanitis on 30/12/2014.
 */
trait FeedbackBase extends Controller {

  val emailConfiguration: EmailConfiguration

  def sendFeedback(contents: String): Unit = {

    import scala.language.postfixOps

    import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

    implicit val implicitEmailConf = implicitly[EmailConfiguration](emailConfiguration)

    val template: Contents = FeedbackMessageBuilder.buildWith(contents)

    SEND email template withSubject "Feedback" to emailConfiguration.feedbackEmail.email send

  }

}
