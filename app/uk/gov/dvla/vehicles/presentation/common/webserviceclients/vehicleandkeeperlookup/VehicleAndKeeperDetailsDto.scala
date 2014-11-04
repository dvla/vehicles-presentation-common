package webserviceclients.vehicleandkeeperlookup

import org.joda.time.DateTime
import play.api.libs.json.Json

final case class VehicleAndKeeperDetailsDto(registrationNumber: String,
                                            vehicleMake: Option[String],
                                            vehicleModel: Option[String],
                                            keeperTitle: Option[String],
                                            keeperFirstName: Option[String],
                                            keeperLastName: Option[String],
                                            keeperAddressLine1: Option[String],
                                            keeperAddressLine2: Option[String],
                                            keeperAddressLine3: Option[String],
                                            keeperAddressLine4: Option[String],
                                            keeperPostTown: Option[String],
                                            keeperPostcode: Option[String],
                                            keeperEndDate: Option[DateTime])

object VehicleAndKeeperDetailsDto {

  // TODO : I Suspect that we'll need this mapping for dates, but add it when testing demonstrates the need
  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
//  implicit object DateTimeJsonFormat extends JsonFormat[DateTime] {
//    def write(dateTime: DateTime) = {
//      val formatter = ISODateTimeFormat.dateTime
//      JsString(formatter.print(dateTime))
//    }
//
//    def read(value: JsValue) = value match {
//      case JsString(isoString) =>
//        ISODateTimeFormat.dateTime().parseDateTime(isoString)
//      case _ => deserializationError("Could not deserialize the iso date time")
//    }
//  }

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsDto]
}