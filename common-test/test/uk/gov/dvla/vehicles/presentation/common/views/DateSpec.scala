package uk.gov.dvla.vehicles.presentation.common.views

import org.joda.time.LocalDate
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.pages.{DatePage, ErrorPanel}

class DateSpec extends UiSpec with TestHarness {

  val today = LocalDate.now()

  "Date" should {
    "set today's date if the button is clicked" in new WebBrowserWithJs {
      go to DatePage.instance

      DatePage.instance.waitUntilJavascriptReady
      click on DatePage.useTodaysDateButton

      DatePage.instance.required.day.value should equal(today.toString("dd"))
      DatePage.instance.required.month.value should equal(today.toString("MM"))
      DatePage.instance.required.year.value should equal(today.toString("YYYY"))
    }

    "only allow numbers to be typed in" in new WebBrowserWithJs {
      val alphaChars = "#$%&'()*+,-./:;<=>?@[\\]^{|}~ \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
      go to DatePage.instance
      DatePage.instance.waitUntilJavascriptReady

      DatePage.instance.required.day.underlying.sendKeys(alphaChars.concat(today.toString("dd")))
      DatePage.instance.required.month.underlying.sendKeys(alphaChars.concat(today.toString("MM")))
      DatePage.instance.required.year.underlying.sendKeys(alphaChars.concat(today.toString("YYYY")))

      DatePage.instance.required.day.value should equal(today.toString("dd"))
      DatePage.instance.required.month.value should equal(today.toString("MM"))
      DatePage.instance.required.year.value should equal(today.toString("YYYY"))
    }
  }

  "Required date of birth" should {
    "Not allow any empty fields" in new WebBrowserForSelenium {
      DatePage.instance.navigate("01", "01", "1984", "", "", "")
      ErrorPanel.text should include(Messages("error.date.invalid"))
      ErrorPanel.numberOfErrors should equal(1)
    }
  }
}
