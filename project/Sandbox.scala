import java.io.FileOutputStream
import java.net.URLClassLoader
import org.apache.commons.io.IOUtils
import sbt._
import sbt.Keys._
import Resolvers._

object Sandbox extends Plugin {

  def sandPrj(name: String, version: String): (Project, ScopeFilter) = (
    Project(name, file(s"target/sandbox/$name"))
      .settings(libraryDependencies ++= Seq("dvla" %% name % version))
      .settings(resolvers ++= projectResolvers)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*),
    ScopeFilter(inProjects(LocalProject(name)), inConfigurations(Runtime))
    )

  lazy val (osAddressLookup, scopeOsAddressLookup) = sandPrj("os-address-lookup", "0.1")
  lazy val (vehiclesLookup, scopeVehiclesLookup) = sandPrj("vehicles-lookup", "0.1")
  lazy val (vehiclesDisposeFulfil, scopeVehiclesDisposeFulfil) = sandPrj("vehicles-dispose-fulfil", "0.1")

  lazy val sandboxedProjects = Seq(osAddressLookup, vehiclesLookup, vehiclesDisposeFulfil)

  lazy val vehiclesOnline = ScopeFilter(inProjects(ThisProject), inConfigurations(Runtime))

  lazy val runMicroServices = taskKey[Unit]("Runs all the required by the sandbox micro services'")
  lazy val runMicroServicesTask = runMicroServices := {

    def runPrj(prjClassPath: Seq[Attributed[File]], classDirectory: File, props: String, fileName: String): Unit = {
      val prjClassloader = new URLClassLoader(
        prjClassPath.map(_.data.toURI.toURL).toArray,
        this.getClass.getClassLoader.getParent.getParent
      )
      val f = new java.io.File(classDirectory, s"$fileName.conf")
      f.getParentFile.mkdirs()
      IOUtils.write(props, new FileOutputStream(f))

      Thread.currentThread().setContextClassLoader(prjClassloader)

      import scala.reflect.runtime.universe.runtimeMirror
      import scala.reflect.runtime.universe.newTermName
      lazy val mirror = runtimeMirror(prjClassloader)
      val bootSymbol = mirror.staticModule("dvla.microservice.Boot").asModule
      val boot = mirror.reflectModule(bootSymbol).instance
      val mainMethodSymbol = bootSymbol.typeSignature.member(newTermName("main")).asMethod
      val bootMirror = mirror.reflect(boot)
      bootMirror.reflectMethod(mainMethodSymbol).apply(Array[String]())
    }

    runPrj(
      fullClasspath.all(scopeOsAddressLookup).value.flatten,
      classDirectory.all(scopeOsAddressLookup).value.head,
      """ordnancesurvey.requesttimeout = "9999"
        |ordnancesurvey.apiversion = "testing"
        |ordnancesurvey.beta06.username = "testUser"
        |ordnancesurvey.beta06.password = "testPass"
        |ordnancesurvey.beta06.baseurl = "https://localhost/ord-serv:1234"
        |ordnancesurvey.preproduction.apikey = "someApiKey"
        |ordnancesurvey.preproduction.baseurl = "http://baseUrl"""".stripMargin,
      osAddressLookup.id
    )
    runPrj(
      fullClasspath.all(scopeVehiclesLookup).value.flatten,
      classDirectory.all(scopeVehiclesLookup).value.head,
      """getVehicleDetails.baseurl = "http://localhost:8084/GetVehicleDetailsImpl"
        |APPLICATION_CD = "WEBDTT"
        |CHANNEL_CD = "WEBDTT"
        |SERVICE_TYPE_CD = "E"
        |CONTACT_ID = "1"""".stripMargin,
      vehiclesLookup.id
    )
    runPrj(
      fullClasspath.all(scopeVehiclesDisposeFulfil).value.flatten,
      classDirectory.all(scopeVehiclesDisposeFulfil).value.head,
      """vss.baseurl = "http://localhost:8085/demo/services/DisposeToTradeService"
        |APPLICATION_CD = "WEBDTT"
        |SERVICE_TYPE_CD = "WEBDTT"
        |ORG_BUSINESS_UNIT = "WEBDTT"""".stripMargin,
      vehiclesDisposeFulfil.id
    )
  }

  lazy val sandbox = taskKey[Unit]("Runs the whole sandbox for manual testing including microservices, webapp and legacy stubs'")
  lazy val sandboxTask = sandbox <<= (runMicroServices, (run in Runtime).toTask("")) { (body, stop) =>
    body doFinally stop
  }
}