package uk.gov.dvla.vehicles.presentation.common.controllers

import com.github.tomakehurst.wiremock.client.WireMock.{get, urlEqualTo, aResponse}
import java.net.URL
import org.apache.commons.io.{FileUtils, IOUtils}
import org.scalatest.BeforeAndAfterAll
import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader
import uk.gov.dvla.vehicles.presentation.common.{UnitSpec, TestWithApplication}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture

class VersionUnitSpec extends UnitSpec with BeforeAndAfterAll with WireMockFixture {
  val buildDetails = "Name: test\nVersion: 0.01\n\n"
  val tmpFile = java.io.File.createTempFile("VersionUnitSpecTmpFile", "tmp")

  override def beforeAll() {
    FileUtils.writeStringToFile(tmpFile, buildDetails)
  }

  override def afterAll() {
    tmpFile.delete()
  }

  "version" should {
    "show the build-details.txt if exists along with runtime information" in new TestWithApplication {
      val loader = new URLClassLoader(Seq[URL](), getClass.getClassLoader) {
        override def loadClass(name: String) = {
          if (name.startsWith(classOf[Version].getName)) {
            val cls = super.getResource(name.replace(".", "/") + ".class").openStream()
            val clsBytes = IOUtils.toByteArray(cls)
            defineClass(name, clsBytes, 0, clsBytes.length)
          } else super.loadClass(name)
        }
        override def getResource(name: String) = {
          if (name == "build-details.txt") tmpFile.toURI.toURL
          else super.getResource(name)
        }
      }

      val request = FakeRequest()

      import scala.reflect.runtime.{universe => ru}
      val mirror = ru.runtimeMirror(loader)
      val classVersion = ru.typeOf[Version].typeSymbol.asClass
      val cm = mirror.reflectClass(classVersion)
      val constructorMethod = ru.typeOf[Version].declaration(ru.nme.CONSTRUCTOR).asMethod
      val constructor = cm.reflectConstructor(constructorMethod)

      wireMock.register(get(urlEqualTo("/version")).willReturn(aResponse()))
      val versionController = constructor(Seq(s"http://localhost:$wireMockPort/version"))

      val versionSymbol = ru.typeOf[Version].member(ru.newTermName("version"))
      val im = mirror.reflect(versionController)
      val versionMethodMirror = im.reflectMethod(versionSymbol.asMethod).apply().asInstanceOf[Action[AnyContent]]

      val result = versionMethodMirror(request)

      val resultContent = contentAsString(result)

      resultContent should include(buildDetails)
      resultContent should include("Running as:")
      resultContent should include("Runtime OS:")
      resultContent should include("Runtime Java:")
    }

    "show build-details.txt doesn't exist along with runtime information" in {
      val request = FakeRequest()

      val versionController = new Version
      val resultContent = contentAsString(versionController.version(request))

      resultContent should include("No build details /build-details.txt")
      resultContent should include("Runtime Java:")
    }

    "fetch the version strings from microservices" in new TestWithApplication {
      wireMock.register(get(urlEqualTo("/version1")).willReturn(aResponse().withBody("version1-body")))
      wireMock.register(get(urlEqualTo("/version2")).willReturn(aResponse().withBody("version2-body")))
      wireMock.register(get(urlEqualTo("/version3")).willReturn(aResponse().withBody("version3-body")))

      val versionController = new Version(
        s"http://localhost:$wireMockPort/version1",
        s"http://localhost:$wireMockPort/version2",
        s"http://localhost:$wireMockPort/version3"
      )

      val versionString = contentAsString(versionController.version(FakeRequest()))
      versionString should include("version1-body")
      versionString should include("version2-body")
      versionString should include("version2-body")
    }

    "fetch the version strings from not existing url" in new TestWithApplication {
      val versionController = new Version("http://localh:36234/test", "http://localh:36234/test2")
      val versionString = contentAsString(versionController.version(FakeRequest()))

      versionString should include("http://localh:36234/test")
      versionString should include("http://localh:36234/test2")
      versionString should include("NettyConnectListener")
    }

    "fetch the version sting from a non parsing url" in new TestWithApplication {
      val versionController = new Version("not parsing url 1", "not parsing url 2")
      val versionString = contentAsString(versionController.version(FakeRequest()))

      versionString should include("not parsing url 1")
      versionString should include("not parsing url 2")
    }
  }
}
