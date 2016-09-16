package uk.gov.dvla.vehicles.presentation.common.controllers

import java.net.URL
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.sequence
import scala.io.Source.fromInputStream
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

class Version(msVersionUrls: String*) extends Controller {

  def version = Action.async { implicit request =>
    def prop(name: String) = sys.props.getOrElse(name, "Unknown")
    val buildDetails = Option(getClass.getResource("/build-details.txt"))
      .fold("No build details /build-details.txt doesn't exist in the classpath") {
        detailsStream => fromInputStream(detailsStream.openStream()).mkString
      }

    def result(msVersions: Seq[String]) = Ok(
//      versionWidget(
        s"""$buildDetails
         |Running as: ${prop("user.name")}@${java.net.InetAddress.getLocalHost.getHostName}
         |Runtime OS: ${prop("os.name")}-${prop("os.version")}
         |Runtime Java: ${prop("java.version")} ${prop("java.vendor")}
         |
         |==========================================================
         |
         |${msVersions.foldLeft("")((result, version) => result + version + "\n----------------------------------------------------------\n")}
       """.stripMargin
//    )
    )

    def fetchVersion(url: String) = {
      Try(new URL(url)).map(_.toURI.toString) match {
        case Success(validUrl) =>
          WS.url(validUrl).get().map(_.body) recover {
            case NonFatal(e) => s"Cannot fetch version from url $validUrl because of\n${e.getStackTrace}"
          }
        case Failure(t) =>
          Future.successful(s"Cannot parse the given version url: $url . The error is : ${t.getStackTrace}")
      }
    }

    sequence(msVersionUrls map fetchVersion) map result
  }
}

object Version {
  final val Suffix = "/version"
}
