package webserviceclients.address_lookup

import models.AddressModel

import scala.concurrent.Future
import play.api.i18n.Lang

trait AddressLookupService {

  def fetchAddressesForPostcode(postcode: String, trackingId: String)
                               (implicit lang: Lang): Future[Seq[(String, String)]]

  def fetchAddressForUprn(uprn: String, trackingId: String)
                         (implicit lang: Lang): Future[Option[AddressModel]]
}
