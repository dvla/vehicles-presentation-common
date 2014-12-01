package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import org.joda.time.DateTime

final case class VssWebHeaderDto (transactionId: String,
                                  originDateTime: DateTime,
                                  applicationCode: String,
                                  serviceTypeCode: String,
                                  endUser: VssWebEndUserDto)
