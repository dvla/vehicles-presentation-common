package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

final case class DmsWebEndUserDto(endUserTeamCode: String,
                                  endUserTeamDesc: String,
                                  endUserRole: String,
                                  endUserId: String,
                                  endUserIdDesc: String,
                                  endUserLongNameDesc: String)
