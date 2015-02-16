package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import org.joda.time.DateTime
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsNumber, JsResult, JsValue, Reads, JsSuccess, JsError, JsString, JsPath, Writes, Json}

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
                                            disposeFlag: Option[Boolean],
                                            keeperEndDate: Option[DateTime],
                                            suppressedV5Flag: Option[Boolean])

object VehicleAndKeeperDetailsDto {

  implicit val jodaDateReads: Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    private val df = org.joda.time.format.ISODateTimeFormat.dateTime()

    private def parseDate(input: String): Option[DateTime] =
      scala.util.control.Exception.allCatch[DateTime] opt (DateTime.parse(input, df))

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
      case JsString(s) => parseDate(s) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", "ISO8601"))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }
  }

  implicit val jodaDateWrites: Writes[org.joda.time.DateTime] = new Writes[org.joda.time.DateTime] {
    def writes(d: org.joda.time.DateTime): JsValue = JsString(d.toString())
  }

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsDto]
}
