package uk.gov.dvla.vehicles.presentation.common.models

import play.api.data.Forms.mapping
import uk.gov.dvla.vehicles.presentation.common.mappings.BusinessName.businessNameMapping

case class BusinessNameModel(name: String)

object BusinessNameModel {

  object Form {
    final val BusinessNameId = "name"

    final val Mapping =  mapping(
      BusinessNameId -> businessNameMapping
    )(BusinessNameModel.apply)(BusinessNameModel.unapply)
  }
}
