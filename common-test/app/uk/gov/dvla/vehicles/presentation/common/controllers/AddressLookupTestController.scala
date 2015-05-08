package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressDto
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookupTestController @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends AddressLookup(addressLookup) {

  override def byPostcode(postCode: String) = Action.async { request =>
    implicit val writes = Json.format[AddressDto]
    if (postCode == "123")
      Future.successful{InternalServerError("Some error message")}
    else if (postCode == "456")
      Future.successful(Ok(Json.toJson(Seq[AddressDto]())))
    else {
      Future.successful(Ok(Json.toJson(Seq(
        AddressDto(s"A1, A2, A3, A4 $postCode", None, "a1", Some("a2"), Some("a3"), "a4", postCode),
        AddressDto(s"B1, B2, B3, B4 $postCode", None, "b1", Some("b2"), Some("b3"), "b4", postCode),
        AddressDto(s"C1, C2, C3, C4 $postCode", None, "c1", Some("c2"), Some("c3"), "c4", postCode),
        AddressDto(s"D1, D2, D3, D4 $postCode", None, "d1", Some("d2"), Some("d3"), "d4", postCode)
      ))))
    }
  }
}