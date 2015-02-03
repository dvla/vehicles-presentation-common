package uk.gov.dvla.vehicles.presentation.common.views.helpers

import views.html.helper.FieldConstructor
import uk.gov.dvla.vehicles.presentation.common.views

object EmptyTemplate {
  implicit val fieldConstructor = FieldConstructor(views.html.widgets.templates.emptyTemplate.f)
}
