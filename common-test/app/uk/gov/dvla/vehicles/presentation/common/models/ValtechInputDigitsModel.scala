package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms.{mapping, nonEmptyText}

case class ValtechInputDigitsModel(mileage: String)

object ValtechInputDigitsModel {

  object Form {
    final val MileageId = "mileage"

    final val Mapping = mapping(
      MileageId -> nonEmptyText()
    )(ValtechInputDigitsModel.apply)(ValtechInputDigitsModel.unapply)

  }
}
