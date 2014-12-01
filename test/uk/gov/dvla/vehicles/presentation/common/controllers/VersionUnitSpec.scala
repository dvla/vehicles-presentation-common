package uk.gov.dvla.vehicles.presentation.common.controllers

import java.io.ByteArrayInputStream
import java.net.URL

import com.github.tomakehurst.wiremock.client.MappingBuilder
import play.api.test.Helpers.{BAD_REQUEST, LOCATION, OK, SET_COOKIE, contentAsString, defaultAwaitTimeout}
import com.github.tomakehurst.wiremock.http.RequestMethod
import org.apache.commons.io.{FileUtils, IOUtils}
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import play.api.libs.Files.TemporaryFile
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.dvla.vehicles.presentation.common.{WithApplication, UnitSpec}

import scala.concurrent.Future
import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader
import org.scalatest.concurrent.Futures
import uk.gov.dvla.vehicles.presentation.common.testhelpers.WireMockFixture

class VersionUnitSpec extends UnitSpec with BeforeAndAfterAll with WireMockFixture {
  val buildTimeDetails = "Build time details"
  val tmpFile = java.io.File.createTempFile("VersionUnitSpecTmpFile", "tmp")

  override def beforeAll() {
    FileUtils.writeStringToFile(tmpFile, "buildTimeDetails")
  }

  override def afterAll() {
    tmpFile.delete()
  }

  "version" should {
//    "show the build-details.txt if exists along with runtime information" in {
//      val loader = new URLClassLoader(Seq[URL](), getClass.getClassLoader) {
//        override def loadClass(name: String) = {
//          if (name == classOf[Version].getName) {
//            val cls = super.getResource(classOf[Version].getName.replace(".", "/") + ".class").openStream()
//            val clsBytes = IOUtils.toByteArray(cls)
//            defineClass(classOf[Version].getName, clsBytes, 0, clsBytes.length)
//          } else super.loadClass(name)
//        }
//        override def getResource(name: String) = {
//          println(s"################# $name")
//          if (name == "/build-details.txt") tmpFile.toURI.toURL
//          else super.getResource(name)
//        }
//      }
//      val request = FakeRequest()
//
//      val testVersionControllerClass = loader.loadClass(classOf[Version].getName)
//      val versionController = testVersionControllerClass.newInstance()
//      val result = testVersionControllerClass
//        .getMethod("version").invoke(versionController, request)
//        .asInstanceOf[Future[Result]]
//
//      println(s"################ ${versionController.getClass.getClassLoader} ")
//      val resultContent = contentAsString(result)
//
//      resultContent should include(buildTimeDetails)
//      resultContent should include("Runtime Java:")
//    }

    "show build-details.txt doesn't exist along with runtime information" in {
      val request = FakeRequest()

      val versionController = new Version
      val resultContent = contentAsString(versionController.version(request))

      resultContent should include("No build details /build-details.txt")
      resultContent should include("Runtime Java:")
    }

    "fetch the version strings from microservices" in new WithApplication {
      import com.github.tomakehurst.wiremock.client.WireMock.{get, urlEqualTo, aResponse}
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

    "fetch the version strings from not existing url" in new WithApplication {
      val versionController = new Version("http://localhost:36234/test", "http://localhost:36234/test2")
      val versionString = contentAsString(versionController.version(FakeRequest()))

      versionString should include("http://localhost:36234/test")
      versionString should include("http://localhost:36234/test2")
      versionString should include("NettyConnectListener")
    }

    "fetch the version sting from a non parsing url" in new WithApplication {
      val versionController = new Version("not parsing url 1", "not parsing url 2")
      val versionString = contentAsString(versionController.version(FakeRequest()))

      versionString should include("not parsing url 1")
      versionString should include("not parsing url 2")
    }
  }
}
