package uk.gov.dvla.vehicles.presentation.common.controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.mappings.Postcode
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.AddressLookupService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookupTestController @Inject()(addressLookup: AddressLookupService)
                             (implicit clientSideSessionFactory: ClientSideSessionFactory)
  extends AddressLookup(addressLookup) {

  override def byPostcode(postCode: String) = Action.async { request =>
    implicit val writes = Json.format[AddressDTO]
    if (postCode == "123")
      Future.successful{InternalServerError("Some error message")}
    else if (postCode == "456")
      Future.successful(Ok(Json.toJson(Seq[AddressDTO]())))
    else {
      Future.successful(Ok(Json.toJson(Seq(
        AddressDTO(s"A1, A2, A3, A4 $postCode", "a1", Some("a2"), Some("a3"), "a4", postCode),
        AddressDTO(s"B1, B2, B3, B4 $postCode", "b1", Some("b2"), Some("b3"), "b4", postCode),
        AddressDTO(s"C1, C2, C3, C4 $postCode", "c1", Some("c2"), Some("c3"), "c4", postCode),
        AddressDTO(s"D1, D2, D3, D4 $postCode", "d1", Some("d2"), Some("d3"), "d4", postCode)
      ))))
    }
  }
}