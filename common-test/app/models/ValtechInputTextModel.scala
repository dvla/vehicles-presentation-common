package models

import play.api.data.Forms.{mapping, nonEmptyText}

case class ValtechInputTextModel(inputText: String)

object ValtechInputTextModel {

  object Form {
    final val InputTextId = "inputText"

    final val Mapping = mapping(
      InputTextId -> nonEmptyText(minLength = 11, maxLength = 11)
    )(ValtechInputTextModel.apply)(ValtechInputTextModel.unapply)

  }
}
