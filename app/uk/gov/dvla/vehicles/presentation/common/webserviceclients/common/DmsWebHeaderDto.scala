package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import org.joda.time.DateTime

final case class DmsWebHeaderDto (conversationId: String,
                                  originDateTime: DateTime,
                                  applicationCode: String,
                                  channelCode: String,
                                  contactId: Long,
                                  eventFlag: Boolean,
                                  serviceTypeCode: String,
                                  languageCode: String,
                                  endUser: DmsWebEndUserDto)
