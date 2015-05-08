package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.DmsWebHeaderDto

final case class VehicleAndKeeperLookupRequest(dmsHeader: DmsWebHeaderDto,
                                                referenceNumber: String,
                                                registrationNumber: String,
                                                transactionTimestamp: DateTime)

object VehicleAndKeeperLookupRequest {

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  implicit val JsonFormat = Json.format[VehicleAndKeeperLookupRequest]
}
