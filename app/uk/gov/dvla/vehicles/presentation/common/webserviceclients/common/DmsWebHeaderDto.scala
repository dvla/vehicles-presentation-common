package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{JsString, JsValue, Writes, Json}

final case class DmsWebHeaderDto (conversationId: String,
                                  originDateTime: DateTime,
                                  applicationCode: String,
                                  channelCode: String,
                                  contactId: Long,
                                  eventFlag: Boolean,
                                  serviceTypeCode: String,
                                  languageCode: String,
                                  endUser: Option[DmsWebEndUserDto])

object DmsWebHeaderDto {

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  implicit val JsonFormat = Json.format[DmsWebHeaderDto]
}