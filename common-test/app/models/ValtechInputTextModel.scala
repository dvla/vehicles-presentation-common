package models

import play.api.data.Forms.{mapping, nonEmptyText}

case class ValtechInputTextModel(documentReferenceNumber: String)

object ValtechInputTextModel {

  object Form {
    final val DocumentReferenceNumberId = "documentReferenceNumber"

    final val Mapping = mapping(
      DocumentReferenceNumberId -> nonEmptyText(minLength = 11, maxLength = 11)
    )(ValtechInputTextModel.apply)(ValtechInputTextModel.unapply)

  }
}
