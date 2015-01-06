package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.mvc.{Call, Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm
import uk.gov.dvla.vehicles.presentation.common.views

class FeedbackFormController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  implicit val controls: Map[String, Call] = Map(
    "submit" -> uk.gov.dvla.vehicles.presentation.common.controllers.routes.FeedbackFormController.submit()
  )

  implicit val token: uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.CsrfPreventionToken = new CsrfPreventionToken("123")

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
          val msg = s"Success - you entered value of ${validForm.feedback}"
          Ok(views.html.success(msg))
        }
      )
    }
  }
}
