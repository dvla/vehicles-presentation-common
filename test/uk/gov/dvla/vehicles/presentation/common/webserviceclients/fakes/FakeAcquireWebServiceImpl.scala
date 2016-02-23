package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import play.api.http.Status.{FORBIDDEN, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.Logger
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients
import webserviceclients.acquire.AcquireResponse
import webserviceclients.acquire.AcquireResponseDto
import webserviceclients.acquire.AcquireRequestDto
import webserviceclients.acquire.AcquireWebService
import webserviceclients.common.MicroserviceResponse

class FakeAcquireWebServiceImpl extends AcquireWebService {
  import FakeAcquireWebServiceImpl.SimulateForbidden
  import FakeAcquireWebServiceImpl.SimulateMicroServiceUnavailable
  import FakeAcquireWebServiceImpl.SimulateSoapEndpointFailure
  import FakeAcquireWebServiceImpl.acquireResponseGeneralError
  import FakeAcquireWebServiceImpl.acquireResponseSoapEndpointFailure
  import FakeAcquireWebServiceImpl.acquireResponseSuccess

  override def callAcquireService(request: AcquireRequestDto, trackingId: TrackingId): Future[WSResponse] = {
    if (request.referenceNumber == SimulateMicroServiceUnavailable) {
      Future.failed(new Exception("Connection refused"))
    } else {
      val (status: Int, acquireResponse: AcquireResponseDto) = {
        request.referenceNumber match {
          case SimulateSoapEndpointFailure => (SERVICE_UNAVAILABLE, acquireResponseSoapEndpointFailure)
          case SimulateForbidden => (FORBIDDEN, acquireResponseGeneralError)
          case _ => (OK, acquireResponseSuccess)
        }
      }
      val responseAsJson = Json.toJson(acquireResponse)
      Logger.debug(s"FakeVehicleLookupWebService callVehicleLookupService with: $responseAsJson")
      Future.successful(new FakeResponse(status = status, fakeJson = Some(responseAsJson)))
    }
  }
}

object FakeAcquireWebServiceImpl {
  final val TransactionIdValid = "1234"
  final val SimulateForbidden = "7" * 11
  final val SimulateMicroServiceUnavailable = "8" * 11
  final val SimulateSoapEndpointFailure = "9" * 11
  private final val RegistrationNumberValid = "AB12AWR"

  val acquireResponseSuccess = AcquireResponseDto(
    None,
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = RegistrationNumberValid)
  )

  // We should always get back a transaction id even for failure scenarios.
  // Only exception is if the soap endpoint is down
  val acquireResponseGeneralError = AcquireResponseDto(
    Some(MicroserviceResponse("", "ms.vehiclesService.error.generalError")),
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = "")
  )

  // No transactionId because the soap endpoint is down
  val acquireResponseSoapEndpointFailure = AcquireResponseDto(
    None,
    AcquireResponse(transactionId = "", registrationNumber = "")
  )

  val acquireResponseApplicationBeingProcessed = AcquireResponseDto(
    None,
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = RegistrationNumberValid)
  )

  val acquireResponseFurtherActionRequired = AcquireResponseDto(
    Some(MicroserviceResponse("", "ms.vehiclesService.response.furtherActionRequired")),
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = "")
  )

  final val ConsentValid = "true"
  final val MileageValid = "20000"
  final val MileageInvalid = "INVALID"
}
