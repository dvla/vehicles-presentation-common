package uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.brute_force_protection

import play.api.http.Status.{FORBIDDEN, OK}
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.fakes.FakeResponse

final class FakeBruteForcePreventionWebServiceImpl() extends BruteForcePreventionWebService {
  import FakeBruteForcePreventionWebServiceImpl._

  override def callBruteForce(vrm: String): Future[WSResponse] = Future {
    vrm match {
      case VrmLocked => FakeResponse(status = FORBIDDEN)
      case _ => FakeResponse(status = OK, fakeJson = responseFirstAttempt)
    }
  }

  override def reset(vrm: String): Future[WSResponse] = Future { FakeResponse(status = OK) }
}

object FakeBruteForcePreventionWebServiceImpl {
  final val VrmAttempt2 = "ST05YYB"
  final val VrmLocked = "ST05YYC"
  final val VrmThrows = "ST05YYD"
  final val MaxAttempts = 3
  lazy val responseFirstAttempt = Some(Json.parse(s"""{"attempts":0}"""))
  lazy val responseSecondAttempt = Some(Json.parse(s"""{"attempts":1}"""))
}
