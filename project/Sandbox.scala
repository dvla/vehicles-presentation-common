import java.io.FileOutputStream
import java.net.URLClassLoader
import org.apache.commons.io.IOUtils
import sbt._
import sbt.Keys._
import Resolvers._

object Sandbox extends Plugin {

  def sandPrj(name: String, deps: ModuleID*): (Project, ScopeFilter) = (
    Project(name, file(s"target/sandbox/$name"))
      .settings(libraryDependencies ++= deps)
      .settings(resolvers ++= projectResolvers)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*),
    ScopeFilter(inProjects(LocalProject(name)), inConfigurations(Runtime))
  )

  lazy val (osAddressLookup, scopeOsAddressLookup) =
    sandPrj("os-address-lookup","dvla" %% "os-address-lookup" % "0.1-SNAPSHOT")
  lazy val (vehiclesLookup, scopeVehiclesLookup) =
    sandPrj("vehicles-lookup", "dvla" %% "vehicles-lookup" % "0.1-SNAPSHOT")
  lazy val (vehiclesDisposeFulfil, scopeVehiclesDisposeFulfil) =
    sandPrj("vehicles-dispose-fulfil", "dvla" %% "vehicles-dispose-fulfil" % "0.1-SNAPSHOT")
  lazy val (legacyStubs, scopeLegacyStubs) = sandPrj(
    name = "legacy-stubs",
    "dvla-legacy-stub-services" % "legacy-stub-services-service" % "1.0-SNAPSHOT",
    "org.eclipse.jetty" % "jetty-server" % "9.2.1.v20140609",
    "org.eclipse.jetty" % "jetty-servlet" % "9.2.1.v20140609",
    "org.springframework" % "spring-web" % "3.0.7.RELEASE"
  )

  lazy val sandboxedProjects = Seq(osAddressLookup, vehiclesLookup, vehiclesDisposeFulfil, legacyStubs)

  lazy val vehiclesOnline = ScopeFilter(inProjects(ThisProject), inConfigurations(Runtime))

  lazy val runMicroServices = taskKey[Unit]("Runs all the required by the sandbox micro services'")
  lazy val runMicroServicesTask = runMicroServices := {
    def withClassLoader(classLoader: ClassLoader)(code: => Unit) {
      val currentContextClassLoader = Thread.currentThread().getContextClassLoader
      Thread.currentThread().setContextClassLoader(classLoader)
      try code
      finally Thread.currentThread().setContextClassLoader(currentContextClassLoader)
    }

    def runScalaMain(mainClass: String)(prjClassLoader: ClassLoader): Unit = withClassLoader(prjClassLoader) {
      import scala.reflect.runtime.universe.runtimeMirror
      import scala.reflect.runtime.universe.newTermName
      lazy val mirror = runtimeMirror(prjClassLoader)
      val bootSymbol = mirror.staticModule(mainClass).asModule
      val boot = mirror.reflectModule(bootSymbol).instance
      val mainMethodSymbol = bootSymbol.typeSignature.member(newTermName("main")).asMethod
      val bootMirror = mirror.reflect(boot)
      bootMirror.reflectMethod(mainMethodSymbol).apply(Array[String]())
    }

    def runJavaMain(mainClassName: String)(prjClassLoader: ClassLoader): Unit = withClassLoader(prjClassLoader) {
      val mainClass = prjClassLoader.loadClass(mainClassName)
      val mainMethod = mainClass.getMethod("main", classOf[Array[String]])
      mainMethod.invoke(null, Array[String]())
    }

    def runProject(prjClassPath: Seq[Attributed[File]],
                   classDirectory: File,
                   props: String,
                   fileName: String,
                   runMainMethod: (ClassLoader) => Unit = runScalaMain("dvla.microservice.Boot")): Unit = {
      val f = new java.io.File(classDirectory, s"$fileName.conf")
      f.getParentFile.mkdirs()
      IOUtils.write(props, new FileOutputStream(f))

      val prjClassloader = new URLClassLoader(
        prjClassPath.map(_.data.toURI.toURL).toArray,
        getClass.getClassLoader.getParent.getParent
      )

      runMainMethod(prjClassloader)
    }

    runProject(
      fullClasspath.all(scopeOsAddressLookup).value.flatten,
      classDirectory.all(scopeOsAddressLookup).value.head,
      """ordnancesurvey.requesttimeout = "9999"
        |ordnancesurvey.apiversion = "tessd"
        |ordnancesurvey.beta06.username = "werw"
        |ordnancesurvey.beta06.password = "esdfdsf"
        |ordnancesurvey.beta06.baseurl = "https://a"
        |ordnancesurvey.preproduction.apikey = "sdfsdfsdf"
        |ordnancesurvey.preproduction.baseurl = "http://a"""".stripMargin,
      osAddressLookup.id
    )
    runProject(
      fullClasspath.all(scopeVehiclesLookup).value.flatten,
      classDirectory.all(scopeVehiclesLookup).value.head,
      """getVehicleDetails.baseurl = "http://lo"
        |APPLICATION_CD = "sdfdfs"
        |CHANNEL_CD = "erty"
        |SERVICE_TYPE_CD = "E"
        |CONTACT_ID = "1"""".stripMargin,
      vehiclesLookup.id
    )
    runProject(
      fullClasspath.all(scopeVehiclesDisposeFulfil).value.flatten,
      classDirectory.all(scopeVehiclesDisposeFulfil).value.head,
      """vss.baseurl = "http://l"
        |APPLICATION_CD = "oiu"
        |SERVICE_TYPE_CD = "ouew"
        |ORG_BUSINESS_UNIT = "adfadf"""".stripMargin,
      vehiclesDisposeFulfil.id
    )
    runProject(
      fullClasspath.all(scopeLegacyStubs).value.flatten,
      classDirectory.all(scopeLegacyStubs).value.head,
      "",
      legacyStubs.id,
      runJavaMain("service.LegacyServicesRunner")
    )
  }

  lazy val sandbox = taskKey[Unit]("Runs the whole sandbox for manual testing including microservices, webapp and legacy stubs'")
  lazy val sandboxTask = sandbox <<= (runMicroServices, (run in Runtime).toTask("")) { (body, stop) =>
    body doFinally stop
  }
}
