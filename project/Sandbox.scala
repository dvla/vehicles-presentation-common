import java.io.FileOutputStream
import java.net.URLClassLoader
import org.apache.commons.io.IOUtils
import sbt._
import sbt.Keys.{classDirectory, fullClasspath, resolvers, libraryDependencies}
import Resolvers._

object Sandbox extends Plugin {

  def sandPrj(name: String, version: String): (Project, ScopeFilter) = (
    Project(name, file(s"sandbox/$name"))
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

  lazy val sandbox = taskKey[Unit]("Runs the sandbox'")
  lazy val sandboxTask = sandbox := {

    def runPrj(prjClassPath: Seq[Attributed[File]], classDirectory: File, props: String, fileName: String): Thread = {
      val prjClassloader = new URLClassLoader(
        prjClassPath.map(_.data.toURI.toURL).toArray,
        this.getClass.getClassLoader.getParent.getParent
      )
      val f = new java.io.File(classDirectory, fileName)
      f.getParentFile.mkdirs()
      IOUtils.write(props, new FileOutputStream(f))

      val t = new Thread() {
        override def run() {
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
      }
      t.start()
      t
    }

    val cpVehiclesOnline = fullClasspath.all(vehiclesOnline).value.flatten
    val cpVehiclesLookup = fullClasspath.all(scopeVehiclesLookup).value.flatten
    val cpeVehiclesDisposeFulfil = fullClasspath.all(scopeVehiclesDisposeFulfil).value.flatten

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
      "os-address-lookup"
    ).join()
  }
}