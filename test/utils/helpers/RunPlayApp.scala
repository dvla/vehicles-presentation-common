package utils.helpers

import java.io.File
import play.api.{Play, Mode, DefaultApplication}
import play.core.ApplicationProvider
import play.core.server.NettyServer
import scala.util.Success
import scala.util.control.NonFatal

object RunPlayApp extends App {
  args.headOption.orElse(Option(System.getProperty("user.dir"))).map { applicationPath =>
    try {
      val server = new NettyServer(
        new StaticApplication(new File(applicationPath)),
        Option(System.getProperty("http.port")).fold(Option(9000))(
          p => if (p == "disabled") Option.empty[Int] else Option(Integer.parseInt(p))
        ),
        Option(System.getProperty("https.port")).map(Integer.parseInt),
        Option(System.getProperty("http.address")).getOrElse("0.0.0.0")
      )

      Runtime.getRuntime.addShutdownHook(new Thread {
        override def run() {
          server.stop()
        }
      })

      Some(server)
    } catch {
      case NonFatal(e) =>
        println("Oops, cannot start the server.")
        e.printStackTrace()
        None
    }
  }
}


class StaticApplication(applicationPath: File) extends ApplicationProvider {
  val application = new DefaultApplication(applicationPath, this.getClass.getClassLoader, None, Mode.Dev)

  Play.start(application)

  def get = Success(application)
  def path = applicationPath
}