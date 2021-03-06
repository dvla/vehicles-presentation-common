package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms.{mapping, nonEmptyText}

case class ValtechInputTextAreaModel(inputText: String)

object ValtechInputTextAreaModel {

  object Form {
    final val InputTextId = "inputText"

    final val Mapping = mapping(
      InputTextId -> nonEmptyText(minLength = 2, maxLength = 1200)
    )(ValtechInputTextAreaModel.apply)(ValtechInputTextAreaModel.unapply)
  }
}
