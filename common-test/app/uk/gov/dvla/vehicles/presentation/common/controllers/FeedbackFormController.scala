package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.{Form, FormError}
import play.api.mvc.{Call, Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.TrackingId
import common.filters.CsrfPreventionAction.CsrfPreventionToken
import common.model.FeedbackForm
import common.model.FeedbackForm.Form.feedback
import common.services.FeedbackMessageBuilder
import common.views
import common.views.helpers.FormExtensions.formBinding

class FeedbackFormController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  implicit val controls: Map[String, Call] = Map(
    "submit" -> uk.gov.dvla.vehicles.presentation.common.controllers.routes.FeedbackFormController.submit()
  )

  implicit val token: uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken =
    new CsrfPreventionToken("123")

  private[controllers] val form = Form(
    FeedbackForm.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.feedbackFormView(form))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.feedbackFormView(invalidForm)),
        validForm => {
          val fb = FeedbackMessageBuilder.buildWith(validForm, TrackingId("123"))
          Ok(views.html.success(fb.htmlMessage))
        }
      )
    }
  }
}
