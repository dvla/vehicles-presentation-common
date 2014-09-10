package models

import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common.mappings.Mileage.mileage

case class MileageModel(mileage: Option[Int])

object MileageModel {

  object Form {
    final val MileageId = "mileage"

    final val Mapping =  mapping(
      MileageId -> mileage
    )(MileageModel.apply)(MileageModel.unapply)
  }
}
