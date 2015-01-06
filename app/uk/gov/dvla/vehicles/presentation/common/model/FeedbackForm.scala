package uk.gov.dvla.vehicles.presentation.common.model

import play.api.data.Forms._
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey


/**
 * Created by gerasimosarvanitis on 29/12/2014.
 */
case class FeedbackForm(feedback: String)

object FeedbackForm {
  object Form {
    final val feedback = "feedback"


    final val Mapping = mapping(
      feedback -> nonEmptyText(minLength = 2, maxLength = 1200)
    )(FeedbackForm.apply)(FeedbackForm.unapply)
  }

  implicit val JsonFormat = Json.format[FeedbackForm]
  final val FeedbackCacheKey = "feedbackCacheKey"
  implicit val Key = CacheKey[FeedbackForm](value = FeedbackCacheKey)
}
