package viewmodels

import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

final case class DisposeViewModel(referenceNumber: String,
                              registrationNumber: String,
                              dateOfDisposal: DayMonthYear,
                              consent: String,
                              lossOfRegistrationConsent: String,
                              mileage: Option[Int])
