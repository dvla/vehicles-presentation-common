package viewmodels

import play.api.data.Form

case class VehicleLookupViewModel(form: Form[viewmodels.VehicleLookupFormViewModel],
                                  displayExitButton: Boolean, 
                                  surveyUrl: Option[String], 
                                  traderName: String, 
                                  address: Seq[String])
