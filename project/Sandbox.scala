import java.io.StringReader
import java.net.URLClassLoader
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FileUtils
import sbt.Keys._
import sbt._
import scala.sys.process.Process

object CommonResolvers {
  val nexus = "http://rep002-01.skyscape.preview-dvla.co.uk:8081/nexus/content/repositories"

  val projectResolvers = Seq(
    "spray repo" at "http://repo.spray.io/",
    "local nexus snapshots" at s"$nexus/snapshots",
    "local nexus releases" at s"$nexus/releases"
  )
}

object Sandbox extends Plugin {

  val legacyServicesStubsPort = 18086
  val secretProperty = "DECRYPT_PASSWORD"
  val gitHost = "gitlab.preview-dvla.co.uk"
  val secretRepoUrl = s"git@$gitHost:dvla/secret-vehicles-online.git"

  val decryptPassword = sys.props.get(secretProperty) orElse sys.env.get(secretProperty)

  def sandPrj(name: String, deps: ModuleID*): (Project, ScopeFilter) = (
    Project(name, file(s"target/sandbox/$name"))
      .settings(libraryDependencies ++= deps)
      .settings(resolvers ++= CommonResolvers.projectResolvers)
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
    validatePrerequisites()

    val targetFolder = (target in ThisProject).value
    val secretRepoFolder = new File(targetFolder, "secretRepo")
    updateSecretVehiclesOnline(secretRepoFolder)

    runProject(
      secretRepoFolder,
      fullClasspath.all(scopeOsAddressLookup).value.flatten,
      classDirectory.all(scopeOsAddressLookup).value.head,
      Some(ConfigPair(
        "ms/dev/os-address-lookup.conf.enc",
        osAddressLookup.id
      ))
    )
    runProject(
      secretRepoFolder,
      fullClasspath.all(scopeVehiclesLookup).value.flatten,
      classDirectory.all(scopeVehiclesLookup).value.head,
      Some(ConfigPair(
        "ms/dev/vehicles-lookup.conf.enc",
        vehiclesLookup.id,
        updatePropertyPort("getVehicleDetails.baseurl", legacyServicesStubsPort)
      ))
    )
    runProject(
      secretRepoFolder,
      fullClasspath.all(scopeVehiclesDisposeFulfil).value.flatten,
      classDirectory.all(scopeVehiclesDisposeFulfil).value.head,
        Some(ConfigPair(
          "ms/dev/vehicles-dispose-fulfil.conf.enc",
          vehiclesDisposeFulfil.id,
          updatePropertyPort("vss.baseurl", legacyServicesStubsPort)
        ))
    )
    runProject(
      secretRepoFolder,
      fullClasspath.all(scopeLegacyStubs).value.flatten,
      classDirectory.all(scopeLegacyStubs).value.head,
      None,
      runJavaMain("service.LegacyServicesRunner", Array(legacyServicesStubsPort.toString))
    )
  }

  lazy val sandbox = taskKey[Unit]("Runs the whole sandbox for manual testing including microservices, webapp and legacy stubs'")
  lazy val sandboxTask = sandbox <<= (runMicroServices, (run in Runtime).toTask("")) { (body, stop) =>
    body.flatMap(t => stop)
  }

  def validatePrerequisites() {
    print(s"${scala.Console.YELLOW}Verifying git is installed...${scala.Console.RESET}")
    if (Process("git --version").! != 0) {
      println(s"${scala.Console.RED}FAILED.")
      println(s"You don't have git installed. Please install git and try again${scala.Console.RESET}")
      throw new Exception("You don't have git installed. Please install git and try again")
    }

    print(s"${scala.Console.YELLOW}Verifying there is ssh access to $gitHost ...${scala.Console.RESET}")
    if (Process(s"ssh -T git@$gitHost").! != 0) {
      println(s"${scala.Console.RED}FAILED.")
      println(s"You don't have access to $gitHost via ssh. Please import your public key to $gitHost${scala.Console.RESET}")
      throw new Exception(s"You don't have access to $gitHost via ssh. Please import your public key to $gitHost")
    }

    print(s"${scala.Console.YELLOW}Verifying $secretProperty is passed ...${scala.Console.RESET}")
    decryptPassword map(secret => println("done")) orElse {
      println(s"""${scala.Console.RED}FAILED.${scala.Console.RESET}""")
      println(s"""${scala.Console.RED}"$secretProperty" not set. Please set it either as jvm arg of sbt """ +
        s""" "-D$secretProperty='secret'"""" +
        s" or export it in the environment with export $secretProperty='some secret prop' ${scala.Console.RESET}")
      throw new Exception(s""" There is no "$secretProperty" set neither as env variable nor as JVM property """)
    }
  }

  def withClassLoader(classLoader: ClassLoader)(code: => Unit) {
    val currentContextClassLoader = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(classLoader)
    try code
    finally Thread.currentThread().setContextClassLoader(currentContextClassLoader)
  }

  def runScalaMain(mainClass: String, args: Array[String] = Array[String]())
                  (prjClassLoader: ClassLoader): Unit = withClassLoader(prjClassLoader) {
    import scala.reflect.runtime.universe.{newTermName, runtimeMirror}
    lazy val mirror = runtimeMirror(prjClassLoader)
    val bootSymbol = mirror.staticModule(mainClass).asModule
    val boot = mirror.reflectModule(bootSymbol).instance
    val mainMethodSymbol = bootSymbol.typeSignature.member(newTermName("main")).asMethod
    val bootMirror = mirror.reflect(boot)
    bootMirror.reflectMethod(mainMethodSymbol).apply(args)
  }

  def runJavaMain(mainClassName: String, args: Array[String] = Array[String]())
                 (prjClassLoader: ClassLoader): Unit = withClassLoader(prjClassLoader) {
    val mainClass = prjClassLoader.loadClass(mainClassName)
    val mainMethod = mainClass.getMethod("main", classOf[Array[String]])
    mainMethod.invoke(null, args)
  }

  case class ConfigPair(encryptedConfig: String,
                        decryptedConfig: String,
                        decryptedTransform: String => String = a => a)

  def runProject(secretRepo: File,
                 prjClassPath: Seq[Attributed[File]],
                 classDirectory: File,
                 configPair: Option[ConfigPair],
                 runMainMethod: (ClassLoader) => Unit = runScalaMain("dvla.microservice.Boot")): Unit = {
    configPair.map { case ConfigPair(encryptedConfig, decryptedConfig, decryptedTransform) =>
      val encryptedConfigFile = new File(secretRepo, encryptedConfig)
      val decryptedConfigFile = new java.io.File(classDirectory, s"$decryptedConfig.conf")
      decryptFile(secretRepo.getAbsolutePath, encryptedConfigFile, decryptedConfigFile, decryptedTransform)
    }

    val prjClassloader = new URLClassLoader(
      prjClassPath.map(_.data.toURI.toURL).toArray,
      getClass.getClassLoader.getParent.getParent
    )

    runMainMethod(prjClassloader)
  }

  def updateSecretVehiclesOnline(secretRepo: File) {
    val secretRepoLocalPath = secretRepo.getAbsolutePath
    val gitOptions = s"--work-tree $secretRepoLocalPath --git-dir $secretRepoLocalPath/.git"


    if (new File(secretRepo, ".git").exists())
      println(Process(s"git $gitOptions pull origin master").!!<)
    else
      println(Process(s"git clone $secretRepoUrl $secretRepoLocalPath").!!<)
  }

  def decryptFile(secretRepo: String, encrypted: File, dest: File, decryptedTransform: String => String) {
    val decryptFile = s"$secretRepo/decrypt-file"
    dest.getParentFile.mkdirs()
    val decryptCommand = s"$decryptFile ${encrypted.getAbsolutePath} ${dest.getAbsolutePath} ${decryptPassword.get}"
    Process(decryptCommand).!!<

    val transformedFile = decryptedTransform(FileUtils.readFileToString(dest))
    FileUtils.writeStringToFile(dest, transformedFile)
  }

  def updatePropertyPort(urlProperty: String, newPort: Int)(properties: String): String = {
    val config = ConfigFactory.parseReader(new StringReader(properties))
    val url = new URL(config.getString(urlProperty))

    val newUrl = new URL(url.getProtocol, url.getHost, newPort, url.getFile).toString

    properties.replace(url.toString, newUrl.toString)
  }
}