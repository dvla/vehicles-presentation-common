package views.helpers

import views.html.helper.FieldConstructor

object EmptyTemplate {
  implicit val fieldConstructor = FieldConstructor(views.html.widgets.templates.emptyTemplate.f)
}