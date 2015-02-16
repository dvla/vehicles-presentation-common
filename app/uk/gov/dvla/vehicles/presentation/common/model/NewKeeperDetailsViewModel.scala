package uk.gov.dvla.vehicles.presentation.common.model

import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.Request
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{CacheKey, ClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.RichCookies
import common.mappings.{TitlePickerString, TitleType}

final case class NewKeeperDetailsViewModel(title: Option[TitleType],
                                           firstName: Option[String],
                                           lastName: Option[String],
                                           dateOfBirth: Option[LocalDate],
                                           driverNumber: Option[String],
                                           businessName: Option[String],
                                           fleetNumber: Option[String],
                                           email: Option[String],
                                           address: AddressModel,
                                           isBusinessKeeper: Boolean,
                                           displayName: String)

object NewKeeperDetailsViewModel {
  implicit val JsonFormatTitleType = Json.format[TitleType]
  implicit val JsonFormat = Json.format[NewKeeperDetailsViewModel]

  implicit def key(implicit prefix: CacheKeyPrefix): CacheKey[NewKeeperDetailsViewModel] =
    CacheKey[NewKeeperDetailsViewModel](newKeeperDetailsCacheKey)

  def newKeeperDetailsCacheKey(implicit prefix: CacheKeyPrefix) = s"${prefix}newKeeperDetails"

  def createNewKeeper(address: AddressModel)(implicit request: Request[_],
                                             clientSideSessionFactory: ClientSideSessionFactory,
                                             prefix: CacheKeyPrefix): Option[NewKeeperDetailsViewModel] = {
    val privateKeeperDetailsOpt = request.cookies.getModel[PrivateKeeperDetailsFormModel]
    val businessKeeperDetailsOpt = request.cookies.getModel[BusinessKeeperDetailsFormModel]

    (privateKeeperDetailsOpt, businessKeeperDetailsOpt) match {
      case (Some(privateKeeperDetails), _) =>
        Some(NewKeeperDetailsViewModel(
          title = None, //Some(privateKeeperDetails.title),
          firstName = Some(privateKeeperDetails.firstName),
          lastName = Some(privateKeeperDetails.lastName),
          dateOfBirth = None, //privateKeeperDetails.dateOfBirth, //TODO put me back
          driverNumber = privateKeeperDetails.driverNumber,
          email = privateKeeperDetails.email,
          address = address,
          businessName = None,
          fleetNumber = None,
          isBusinessKeeper = false,
//          displayName = s"${getTitle(privateKeeperDetails.title)} ${privateKeeperDetails.firstName} ${privateKeeperDetails.lastName}"
          displayName = s"Mr ${privateKeeperDetails.firstName} ${privateKeeperDetails.lastName}"
        ))
      case (_, Some(businessKeeperDetails))  =>
        Some(NewKeeperDetailsViewModel(
          title = None,
          firstName = None,
          lastName = None,
          dateOfBirth = None,
          driverNumber = None,
          email = businessKeeperDetails.email,
          address = address,
          businessName = Some(businessKeeperDetails.businessName),
          fleetNumber = businessKeeperDetails.fleetNumber,
          isBusinessKeeper = true,
          displayName = businessKeeperDetails.businessName
        ))
      case _ => None
    }
  }

  def getTitle(title: TitleType ): String = {
    if (title.titleType > 0 && title.titleType < 4) Messages(TitlePickerString.standardOptions(title.titleType - 1))
    else title.other
  }
}
