package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.{mapping, nonEmptyText, text}
import play.api.libs.json.Json

case class FeedbackForm(rating: String, feedback: String)

object FeedbackForm {

  object Form {
    final val rating = "rating"
    final val feedback = "feedback"

    final val Mapping = mapping(
      rating -> nonEmptyText.verifying(play.api.data.validation.Constraints.pattern("""[1-5]""".r)),
      feedback -> text(maxLength = 500)
    )(FeedbackForm.apply)(FeedbackForm.unapply)
  }

  implicit val JsonFormat = Json.format[FeedbackForm]
}
