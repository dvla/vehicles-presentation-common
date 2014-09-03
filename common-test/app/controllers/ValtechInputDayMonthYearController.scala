package controllers

import com.google.inject.Inject
import models.ValtechInputDayMonthYearModel
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import services.DateServiceImpl
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class ValtechInputDayMonthYearController @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory) extends Controller {

  private val dateService = new DateServiceImpl

  private[controllers] val form = Form(
    ValtechInputDayMonthYearModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.valtechInputDayMonthYearView(form, dateService))
  }

  def submit = Action {
    implicit request => {
      form.bindFromRequest.fold(
        invalidForm => BadRequest(views.html.valtechInputDayMonthYearView(invalidForm, dateService)),
        validForm => Ok(views.html.success(s"Success - you entered date of birth of ${validForm.dateOfBirth.`dd/MM/yyyy`}"))
      )
    }
  }
}
