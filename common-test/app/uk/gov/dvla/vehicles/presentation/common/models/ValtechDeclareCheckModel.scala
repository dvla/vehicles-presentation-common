package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms.{mapping, nonEmptyText}

case class ValtechDeclareCheckModel(checked: String)

object ValtechDeclareCheckModel {

  object Form {
    final val DeclareSelectId = "declareSelectId"

    final val Mapping = mapping(
      DeclareSelectId -> nonEmptyText
    )(ValtechDeclareCheckModel.apply)(ValtechDeclareCheckModel.unapply)
  }
}
