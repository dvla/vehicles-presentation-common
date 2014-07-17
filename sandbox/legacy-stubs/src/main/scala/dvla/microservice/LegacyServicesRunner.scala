package dvla.microservice

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}

object LegacyServicesRunner {

  private val port = sys.props.get("port").getOrElse("46291").toInt
  private val server = new Server(port)

  def main(args: Array[String]) {
    start()
  }

  def start(join:Boolean=true) {

    this.synchronized {
      if (!server.isRunning) {
        org.apache.cxf.transport.servlet.CXFServlet

        val context = new ServletContextHandler(ServletContextHandler.SESSIONS)

        context.setContextPath("/services")
        context.addServlet(new ServletHolder(classOf[DefaultServlet]), "/s")
        server.start()
        if ( join ) server.join()
      }
    }
  }

  def stop() {
    server.stop()
  }
}