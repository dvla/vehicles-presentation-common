package uk.gov.dvla.vehicles.presentation.common.helpers

import org.openqa.selenium.{By, WebDriver}
import org.scalatest.concurrent.Eventually._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}

abstract class UiSpec extends WordSpec with Matchers {
  protected def assertJsTestPass(implicit driver: WebDriver): Unit = {

    val qunitDiv = eventually(timeout(Span(10, Seconds))){
      val qunitDiv = driver.findElement(By.id("qunit"))
      qunitDiv.findElement(By.cssSelector("h2.qunit-fail, h2.qunit-pass"))
    }

    val qunitTestresult = driver.findElement(By.id("qunit-testresult"))
    info(qunitTestresult.getText.lines.map("    " + _).mkString("\n"))
    val qunitTests = driver.findElement(By.id("qunit-tests"))
    info(qunitTests.getText.lines.map("    " + _).mkString("\n"))

    if (qunitDiv.findElement(By.id("qunit-banner")).getAttribute("class").contains("qunit-fail"))
      fail()
  }
}
