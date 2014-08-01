package uk.gov.dvla.vehicles.presentation.common.views.helpers

import play.api.templates.{Html, HtmlFormat}
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction

object CsrfHelper {

  val csrfPrevention = getProperty("csrf.prevention", default = true)

  def hiddenFormField(implicit token: CsrfPreventionAction.CsrfPreventionToken): Html =
    if (csrfPrevention) {
      val csrfTokenName = CsrfPreventionAction.TokenName
      Html(s"""<input type="hidden" name="$csrfTokenName" value="${HtmlFormat.escape(token.value)}"/>""")
    } else Html("")
}