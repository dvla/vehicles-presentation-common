package uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup

import play.api.i18n.Lang
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import scala.concurrent.Future

trait AddressLookupService {

  def fetchAddressesForPostcode(postcode: String, trackingId: String, showBusinessName: Option[Boolean] = None)
                               (implicit lang: Lang): Future[Seq[(String, String)]]

  def fetchAddressForUprn(uprn: String, trackingId: String)
                         (implicit lang: Lang): Future[Option[AddressModel]]
}
