package uk.gov.dvla.vehicles.presentation.common.controllers

import java.io.ByteArrayInputStream
import java.net.URL

import org.apache.commons.io.{FileUtils, IOUtils}
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import play.api.libs.Files.TemporaryFile
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

import scala.concurrent.Future
import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader

class VersionUnitSpec extends UnitSpec with BeforeAndAfterAll {
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
  }
}
