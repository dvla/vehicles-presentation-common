package uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup

import org.joda.time.{DateTimeZone, DateTime}
import play.Logger
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
                                            keeperChangeDate: Option[DateTime],
                                            suppressedV5Flag: Option[Boolean])

object VehicleAndKeeperDetailsDto {
  private final val dtz = DateTimeZone.forID("Europe/London")

  implicit val jodaDateReads: Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    private val df = org.joda.time.format.ISODateTimeFormat.dateTime().withZone(dtz)

    private def parseDate(input: String): Option[DateTime] = {
      Logger.info(s"DE418 parsing date $input into ${DateTime.parse(input, df)}")
      scala.util.control.Exception.allCatch[DateTime] opt (DateTime.parse(input, df))
    }

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) =>
        val result = new DateTime(d.toLong, dtz)
        Logger.info(s"DE418 converting a JsNumber($d) is converted to $result")
        JsSuccess(result)
      case JsString(s) => parseDate(s) match {
        case Some(d) =>
          Logger.info(s"DE418 converting a JsString($d) is converted to $d")
          JsSuccess(d)
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
