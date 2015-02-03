package uk.gov.dvla.vehicles.presentation.common.webserviceclients.common

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{Writes, JsValue, JsString, Json}

final case class VssWebHeaderDto (transactionId: String,
                                  originDateTime: DateTime,
                                  applicationCode: String,
                                  serviceTypeCode: String,
                                  endUser: VssWebEndUserDto)

object VssWebHeaderDto {

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  implicit val JsonFormat = Json.format[VssWebHeaderDto]
}
