package uk.gov.dvla.vehicles.presentation.common.model

import org.joda.time.DateTime
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupDetailsDto
import common.views.constraints.Postcode._
import common.views.constraints.RegistrationNumber._
import common.views.models.{AddressAndPostcodeViewModel, AddressLinesViewModel}

final case class VehicleAndKeeperDetailsModel(registrationNumber: String,
                                              make: Option[String],
                                              model: Option[String],
                                              title: Option[String],
                                              firstName: Option[String],
                                              lastName: Option[String],
                                              address: Option[AddressModel],
                                              disposeFlag: Option[Boolean],
                                              keeperEndDate: Option[DateTime],
                                              keeperChangeDate: Option[DateTime],
                                              suppressedV5Flag: Option[Boolean])

object VehicleAndKeeperDetailsModel {

  // Create a VehicleAndKeeperDetailsDto from the given replacementVRM. We do this in order get the data out of the response from micro-service call
  def from(vehicleAndKeeperDetailsDto: VehicleAndKeeperLookupDetailsDto) = {

    val addressViewModel = {
      val addressLineModel = AddressLinesViewModel(
        vehicleAndKeeperDetailsDto.keeperAddressLine1.getOrElse(""),
        vehicleAndKeeperDetailsDto.keeperAddressLine2,
        vehicleAndKeeperDetailsDto.keeperAddressLine3,
        vehicleAndKeeperDetailsDto.keeperPostTown.getOrElse("")
      )
      vehicleAndKeeperDetailsDto.keeperPostcode match {
        case Some(postCode) => AddressModel.from(
          AddressAndPostcodeViewModel(addressLineModel, formatPostcode(postCode))
        )
        case None => AddressModel(address = addressLineModel.toViewFormat)
      }
    }

    VehicleAndKeeperDetailsModel(
      registrationNumber = formatVrm(vehicleAndKeeperDetailsDto.registrationNumber),
      make = vehicleAndKeeperDetailsDto.vehicleMake,
      model = vehicleAndKeeperDetailsDto.vehicleModel,
      title = {
        vehicleAndKeeperDetailsDto.keeperTitle match {
          case Some(keeperTitle) if keeperTitle.toUpperCase.startsWith("M") => Some(keeperTitle.toUpperCase)
          case _ => None
        }
      },
      firstName = {
        vehicleAndKeeperDetailsDto.keeperFirstName match {
          case Some(keeperFirstName) => Some(keeperFirstName.toUpperCase)
          case _ => None
        }
      },
      lastName = {
        vehicleAndKeeperDetailsDto.keeperLastName match {
          case Some(keeperLastName) => Some(keeperLastName.toUpperCase)
          case _ => None
        }
      },
      address = Some(addressViewModel),
      disposeFlag = vehicleAndKeeperDetailsDto.disposeFlag,
      keeperEndDate = vehicleAndKeeperDetailsDto.keeperEndDate,
      keeperChangeDate = vehicleAndKeeperDetailsDto.keeperChangeDate,
      suppressedV5Flag = vehicleAndKeeperDetailsDto.suppressedV5Flag
    )
  }

  def from(registrationNumber: String) = {
    VehicleAndKeeperDetailsModel(
      registrationNumber = registrationNumber,
      make = None,
      model = None,
      title = None,
      firstName = None,
      lastName = None,
      address = None,
      disposeFlag = None,
      keeperEndDate = None,
      keeperChangeDate = None,
      suppressedV5Flag = None
    )
  }

  implicit val JsonFormat = Json.format[VehicleAndKeeperDetailsModel]

  implicit def key(implicit prefix: CacheKeyPrefix): CacheKey[VehicleAndKeeperDetailsModel] =
    CacheKey[VehicleAndKeeperDetailsModel](value = vehicleAndKeeperLookupDetailsCacheKey)

  def vehicleAndKeeperLookupDetailsCacheKey(implicit prefix: CacheKeyPrefix) =
    s"${prefix}vehicle-and-keeper-lookup-details"
}
