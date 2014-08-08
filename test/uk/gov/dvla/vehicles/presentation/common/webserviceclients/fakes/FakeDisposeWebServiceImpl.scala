package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes

import play.api.Logger
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.Response
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.dispose.{DisposeWebService, DisposeResponseDto, DisposeRequestDto}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeVehicleLookupWebService.RegistrationNumberValid
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class FakeDisposeWebServiceImpl extends DisposeWebService {
  import FakeDisposeWebServiceImpl._

  override def callDisposeService(request: DisposeRequestDto, trackingId: String): Future[Response] = Future {
    val disposeResponse: DisposeResponseDto = {
      request.referenceNumber match {
        case SimulateMicroServiceUnavailable => throw new RuntimeException("simulateMicroServiceUnavailable")
        case SimulateSoapEndpointFailure => disposeResponseSoapEndpointFailure
        case _ => disposeResponseSuccess
      }
    }
    val responseAsJson = Json.toJson(disposeResponse)
    Logger.debug(s"FakeVehicleLookupWebService callVehicleLookupService with: $responseAsJson")
    new FakeResponse(status = OK, fakeJson = Some(responseAsJson)) // Any call to a webservice will always return this successful response.
  }
}

object FakeDisposeWebServiceImpl {
  final val TransactionIdValid = "1234"
  private final val AuditIdValid = "7575"
  private final val SimulateMicroServiceUnavailable = "8" * 11
  private final val SimulateSoapEndpointFailure = "9" * 11

  val disposeResponseSuccess =
    DisposeResponseDto(transactionId = TransactionIdValid,
      registrationNumber = RegistrationNumberValid,
      auditId = AuditIdValid)

  val disposeResponseSoapEndpointFailure =
    DisposeResponseDto(transactionId = "", // No transactionId because the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = None)

  val disposeResponseFailureWithResponseCode =
    DisposeResponseDto(transactionId = TransactionIdValid, // We should always get back a transaction id even for failure scenarios. Only exception is if the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = Some("ms.vehiclesService.response.unableToProcessApplication"))

  val disposeResponseFailureWithDuplicateDisposal =
    DisposeResponseDto(transactionId = TransactionIdValid, // We should always get back a transaction id even for failure scenarios. Only exception is if the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = Some("ms.vehiclesService.response.duplicateDisposalToTrade"))

  val disposeResponseSoapEndpointTimeout =
    DisposeResponseDto(transactionId = "", // No transactionId because the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = None)

  val disposeResponseApplicationBeingProcessed =
    DisposeResponseDto(transactionId = TransactionIdValid,
      registrationNumber = RegistrationNumberValid,
      auditId = AuditIdValid,
      responseCode = None)

  val disposeResponseUnableToProcessApplication =
    DisposeResponseDto(transactionId = "", // No transactionId because the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = Some("ms.vehiclesService.response.unableToProcessApplication"))

  val disposeResponseUndefinedError =
    DisposeResponseDto(transactionId = "", // No transactionId because the soap endpoint is down
      registrationNumber = "",
      auditId = "",
      responseCode = Some("undefined"))

  final val ConsentValid = "true"
  final val MileageValid = "20000"
  final val MileageInvalid = "INVALID"
}
