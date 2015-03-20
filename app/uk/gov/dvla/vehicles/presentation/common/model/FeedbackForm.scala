package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms._
import uk.gov.dvla.vehicles.presentation.common
import common.mappings.Email.email
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey


/**
 * Created by gerasimosarvanitis on 29/12/2014.
 */
case class FeedbackForm(feedback: String, name: Option[String], email: Option[String])

object FeedbackForm {

  object Form {
    final val feedback = "feedback"
    final val nameMapping = "feedbackName"
    final val emailMapping = "feedbackEmail"


    final val Mapping = mapping(
      feedback -> nonEmptyText(minLength = 2, maxLength = 500),
      nameMapping -> optional(text(minLength = 2, maxLength = 60)),
      emailMapping -> optional(email)
    )(FeedbackForm.apply)(FeedbackForm.unapply)
  }

  implicit val JsonFormat = Json.format[FeedbackForm]
}
