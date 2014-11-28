package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source.fromInputStream
import uk.gov.dvla.vehicles.presentation.common.views
import play.api.Play.current
import ExecutionContext.Implicits.global
import views.html.widgets.{version => versionWidget}
import Future.sequence
import scala.util.control.NonFatal

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

    def fetchVersion(url: String) = WS.url(url).get().map(_.body) recover {
      case NonFatal(e) => s"Cannot fetch version from url $url because of\n${e.getStackTraceString}"
    }

    sequence(msVersionUrls map fetchVersion) map result
  }
}
