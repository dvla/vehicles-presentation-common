package uk.gov.dvla.vehicles.presentation.common.controllers

import play.api.mvc.{Action, Controller}

import scala.io.Source.fromInputStream
import uk.gov.dvla.vehicles.presentation.common.views
import views.html.widgets.{version => versionWidget}

class Version extends Controller {

  def version = Action { implicit request =>
    def prop(name: String) = sys.props.getOrElse(name, "Unknown")
    val buildDetails = Option(getClass.getResource("/build-details.txt"))
      .fold("No build details /build-details.txt doesn't exist in the classpath") {
        detailsStream => fromInputStream(detailsStream.openStream()).mkString
      }
    Ok(versionWidget(s"""$buildDetails
         |Running as: ${prop("user.name")}@${java.net.InetAddress.getLocalHost.getHostName}
         |Runtime OS: ${prop("os.name")}-${prop("os.version")}
         |Runtime Java: ${prop("java.version")} ${prop("java.vendor")}
       """.stripMargin
    ))
  }

}
