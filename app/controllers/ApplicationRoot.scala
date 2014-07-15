package controllers

import com.google.inject.Inject
import play.api.Play
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import utils.helpers.Config

/* Controller for redirecting people to the start page if the enter the application using the url "/"
* This allows us to change the start page using the config file without having to change any code. */
final class ApplicationRoot @Inject()(implicit config: Config) extends Controller {

  def index = Action {
    val appContext = Play.configuration.getString("application.context")
    val pathWithoutRoot = config.startUrl.dropWhile(_ == '/')

    val startUrl = appContext.map(_ + pathWithoutRoot)
      .getOrElse("/" + pathWithoutRoot)

    Redirect(startUrl)
  }
}