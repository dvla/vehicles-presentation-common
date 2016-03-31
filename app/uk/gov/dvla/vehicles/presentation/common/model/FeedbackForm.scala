package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.data.{FormError, Mapping}
import play.api.data.format.Formatter
import uk.gov.dvla.vehicles.presentation.common.mappings.Email.email
import play.api.libs.json.Json
import play.api.data.Forms.of
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggleOptional

case class FeedbackForm(feedback: String, webChat: Option[String], name: Option[String], email: Option[String])

object FeedbackForm {

  object Form {
    final val feedback = "feedback"
    final val webChatOption = "feedbackWebChatOption"
    final val webChat = "feedbackWebChat"
    final val nameMapping = "feedbackName"
    final val emailMapping = "feedbackEmail"

    final val Mapping = mapping(
      feedback -> nonEmptyText(minLength = 2, maxLength = 500),
      webChatOption -> OptionalToggleOptional.optional(WebChatFeedback.optional.withPrefix(webChat)),
      nameMapping -> optional(text(minLength = 2, maxLength = 60)),
      emailMapping -> optional(email)
    )(FeedbackForm.apply)(FeedbackForm.unapply)
  }

  implicit val JsonFormat = Json.format[FeedbackForm]
}

object WebChatFeedback {

  def formatter() = new Formatter[String] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      (data.get(FeedbackForm.Form.webChatOption), data.get(key)) match {
        case (None, _) => Right("Webchat was not offered")
        case (_, Some(wc)) if wc.length >= 2 && wc.length <= 500 => Right(wc)
        case _ => Left(Seq(FormError(key, "error.feedback")))
      }
    }

    override def unbind(key: String, value: String): Map[String, String] = Map(
      FeedbackForm.Form.webChat -> value
    )
  }

  def optional: Mapping[String] = of[String](formatter())
}
