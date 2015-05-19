package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory
import uk.gov.dvla.vehicles.presentation.common.model.{SearchFields, Address}
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, AddressPickerPage}
import scala.collection.JavaConversions.asScalaBuffer

class AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {
  "Address picker widget" should {
    "Show all the expected fields" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.assertAddressInputsInvisible()

      widget.postCodeSearch.value should equal("")
      widget.searchButton
      widget.enterManuallyLink
      widget.addressSelect
      widget.addressLine1.value should equal("")
      widget.addressLine2.value should equal("")
      widget.addressLine3.value should equal("")
      widget.town.value should equal("")
      widget.postcode.value should equal("")
      widget.remember.isSelected should equal(false)
    }

    "Lookup container is visible, Dropdown select is invisible and Manual address elements are invisible on load" in
      new PhantomJsByDefault {
        go to AddressPickerPage
        page.title should equal(AddressPickerPage.title)

        val widget = AddressPickerPage.addressPickerDriver
        widget.assertLookupInputVisible()
        widget.assertAddressInputsInvisible()
        widget.assertAddressListInvisible()
    }

    "working manual address entry" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()

      click on widget.enterAddressManuallyLink

      widget.assertLookupInputInvisible()
      widget.assertAddressListInvisible()
      widget.assertAddressInputsVisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "Keep Lookup container and Dropdown select invisible on resubmit for manual lookup" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()

      click on widget.enterAddressManuallyLink

      widget.addressLine2.value = "address 2"
      widget.addressLine3.value = "address 3"

      click on AddressPickerPage.submit

      widget.assertAddressInputsVisible()
      widget.assertLookupInputInvisible()
      widget.assertAddressListInvisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "Validate the address code only if missing " in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.postCodeSearch.value = "AAAAAAA"
      click on AddressPickerPage.submit
      val errors = ErrorPanel.text.lines.filter(_ != "Please check the form").toSeq
      errors should have size(1)
      errors.head should include(Messages("address-picker-1.address-postcode-lookup"))

    }

    "Lookup an address with ajax call" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.assertAddressInputsInvisible()

      widget.search("ABCD")

      widget.assertLookupInputVisible()
      widget.assertAddressListVisible()
      widget.assertAddressInputsInvisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()

      println("OPTIONS:" + widget.addressSelect.getOptions.map(_.getAttribute("value")).mkString("\n"))

      widget.addressSelect.getOptions should not be empty

      widget.addressSelect.value = "0"

      widget.addressLine1.value should equal("a1")
      widget.addressLine2.value should equal("a2")
      widget.addressLine3.value should equal("a3")

      widget.town.value should equal("a4")
      widget.postcode.value should equal("ABCD")

      widget.addressSelect.value = "default"
      widget.addressLine1.value should equal("")
      widget.addressLine2.value should equal("")
      widget.addressLine3.value should equal("")

      widget.town.value should equal("")
      widget.postcode.value should equal("")

      click on widget.changeMyDetailsLink
      widget.assertLookupInputVisible()
      widget.assertAddressInputsInvisible()
      widget.assertAddressListInvisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "show manual input only with javascript disabled" ignore new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver

      widget.assertLookupInputVisible()
      widget.assertAddressInputsVisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "show server error message" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()

      widget.postCodeSearch.value = "123" // 123 is a special postcode that will make the server return 500
      click on widget.searchButton
      widget.assertLookupInputVisible()
      widget.assertAddressInputsInvisible()
      widget.assertAddressListInvisible()
      widget.assertServerErrorVisible()
      widget.assertMissingPostcodeInvisible()
    }

    "show server postcode not found message" in new PhantomJsByDefault {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()

      widget.postCodeSearch.value = "456" // 456 is a special postcode that will make the server return 500
      click on widget.searchButton
      widget.assertLookupInputVisible()
      widget.assertAddressInputsInvisible()
      widget.assertAddressListInvisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeVisible()
    }

    "validate required elements" in new PhantomJsByDefault {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()

      widget.search("AA11AA")
      widget.addressSelect.value = "1"
      widget.addressLine1.value = ""
      widget.addressLine2.value = "address 2"
      widget.addressLine3.value = "address 3"
      widget.assertLookupInputVisible()
      widget.assertAddressListVisible()
      widget.assertAddressInputsVisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()

      click on AddressPickerPage.submit
      page.title should equal(AddressPickerPage.title)
      ErrorPanel.text should include(Messages("error.address.addressLine1"))

      widget.assertLookupInputVisible()
      widget.assertAddressListInvisible()
      widget.assertAddressInputsVisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "preserve the submitted values" in new PhantomJsByDefault {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.search("AA11AA")
      widget.addressSelect.value = "1"
      widget.addressLine1.value = "address 1"
      widget.addressLine2.value = "address 2"
      widget.addressLine3.value = "address 3"
      widget.town.value = "town"
      widget.postcode.value = "" //leave one missing to see if the others are there after submit

      click on AddressPickerPage.submit
      page.title should equal(AddressPickerPage.title)
      ErrorPanel.text should include(Messages("error.address.postCode"))
      widget.addressLine1.value should equal("address 1")
      widget.addressLine2.value should equal("address 2")
      widget.addressLine3.value should equal("address 3")
      widget.town.value should equal("town")
      widget.postcode.value should equal("")
    }

    "submit when required are present" in new PhantomJsByDefault {
      val model = Address(
        SearchFields(true, true, true, Some("AA11AA"), Some("1"), true),
        "address line 1",
        Some("address line 2"),
        Some("address line 3"),
        "Post town",
        "N19 3NN"
      )
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.search("AA11AA")
      widget.addressSelect.value = "1"
      widget.addressLine1.value = model.streetAddress1
      widget.addressLine2.value = model.streetAddress2.get
      widget.addressLine3.value = model.streetAddress3.get
      widget.town.value = model.postTown
      widget.postcode.value = model.postCode
      widget.remember.select()
      click on AddressPickerPage.submit
      page.title should equal("Success") withClue s"Errors: ${ErrorPanel.text}"
      val addressCookie = webDriver.manage().getCookieNamed(AddressPickerModel.Key.value)
      val json = addressCookie.getValue
      println("addressCookie: " + json)
      AddressPickerModel.JsonFormat.reads(Json.parse(json))
        .map(a => a.address1 should equal(model)) orElse(fail("Did not have a AddressPickerModel in the response"))
    }

//    "load model from the cookies and preserve show the view as it was" in new PhantomJsByDefault {
//
//      val model = Address(
//        SearchFields(true, false, true, Some("AA11AA"), Some("1"), true),
//        "address line 1",
//        Some("address line 2"),
//        Some("address line 3"),
//        "Post town",
//        "N19 3NN"
//      )
//
//      withCookie(model)
//      go to AddressPickerPage
//      val widget = AddressPickerPage.addressPickerDriver
//      widget.assertLookupInputVisible()
//      widget.assertAddressListInvisible()
//      widget.assertAddressInputsVisible()
//      widget.postCodeSearch should equal(model.searchFields.postCode)
//      widget.addressLine1.value should equal(model.streetAddress1)
//      widget.addressLine2.value should equal(model.streetAddress2)
//      widget.addressLine3.value should equal(model.streetAddress3)
//      widget.town.value should equal(model.postTown)
//      widget.postcode.value should equal(model.postCode)
//    }
//
//    "should hide the search inputs if cookie specifies so" in new PhantomJsByDefault {
//      val model = Address(
//        SearchFields(false, false, true, Some("AA11AA"), Some("1"), true),
//        "address line 1",
//        Some("address line 2"),
//        Some("address line 3"),
//        "Post town",
//        "N19 3NN"
//      )
//
//      withCookie(model)
//      go to AddressPickerPage
//      val widget = AddressPickerPage.addressPickerDriver
//      widget.assertLookupInputInvisible()
//      widget.assertAddressListInvisible()
//      widget.assertAddressInputsVisible()
//      widget.addressLine1.value should equal(model.streetAddress1)
//      widget.addressLine2.value should equal(model.streetAddress2)
//      widget.addressLine3.value should equal(model.streetAddress3)
//      widget.town.value should equal(model.postTown)
//      widget.postcode.value should equal(model.postCode)
//    }

    "run qunit tests" ignore {
      "qunit tests should pass" in new WebBrowser(webDriver = WebDriverFactory.defaultBrowserPhantomJs) {
        go to AddressPickerPage.jsTestUrl
        assertJsTestPass
      }
    }

//    def withCookie[A](model: A)(implicit webDriver: WebDriver): Unit = {
//      import AddressPickerModel.{Key, SearchFieldsJsonFormat, AddressJsonFormat, JsonFormat}
//      val json = Json.toJson(model).toString()
//      webDriver.manage().addCookie(new Cookie(Key.value, json))
//    }

  }
}
