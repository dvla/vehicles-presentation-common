package uk.gov.dvla.vehicles.presentation.common.views.helpers

import _root_.views.html.helper.FieldConstructor
import uk.gov.dvla.vehicles.presentation.common.views.html.widgets.templates.baseTemplate

object BaseTemplate {
  implicit val fieldConstructor = FieldConstructor(baseTemplate.f)
}
