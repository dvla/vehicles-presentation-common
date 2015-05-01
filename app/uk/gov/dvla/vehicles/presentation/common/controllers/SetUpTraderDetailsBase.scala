package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.data.{Form, FormError}
import play.api.mvc.{Action, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.{SetupTradeDetailsFormModel, CacheKeyPrefix}
import common.model.SetupTradeDetailsFormModel.Form.{TraderEmailId, TraderNameId, TraderPostcodeId}
import common.views.helpers.FormExtensions.formBinding

abstract class SetUpTradeDetailsBase @Inject()()(implicit protected val clientSideSessionFactory: ClientSideSessionFactory,
                                             prefix: CacheKeyPrefix) extends Controller {

  protected def presentResult(model: Form[SetupTradeDetailsFormModel])(implicit request: Request[_]): Result

  protected def invalidFormResult(model: Form[SetupTradeDetailsFormModel])(implicit request: Request[_]): Result

  protected def success(implicit request: Request[_]): Result

  val form = Form(
    SetupTradeDetailsFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    presentResult(form.fill())
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => invalidFormResult(formWithReplacedErrors(invalidForm)),
      validForm => success.withCookie(validForm)
        .discardingCookie(businessKeeperDetailsCacheKey)
    )
  }

  private def formWithReplacedErrors(form: Form[SetupTradeDetailsFormModel]): Form[SetupTradeDetailsFormModel] = {
    form.replaceError(
      TraderNameId, FormError(key = TraderNameId, message = "error.validBusinessName", args = Seq.empty)
    ).replaceError(
      TraderPostcodeId, FormError(key = TraderPostcodeId, message = "error.restricted.validPostcode", args = Seq.empty)
    ).distinctErrors
  }
}
