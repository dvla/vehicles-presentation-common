package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.model.{Address, SearchFields}
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.pages.{AddressPickerPage, ErrorPanel}

class AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {
  "Address picker widget" should {
    "Show all the expected fields" in new WebBrowserWithJs {
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
      new WebBrowserWithJs {
        go to AddressPickerPage
        page.title should equal(AddressPickerPage.title)

        val widget = AddressPickerPage.addressPickerDriver
        widget.assertLookupInputVisible()
        widget.assertAddressInputsInvisible()
        widget.assertAddressListInvisible()
    }

    "working manual address entry" in new WebBrowserWithJs {
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

    "Keep Lookup container and Dropdown select invisible on resubmit for manual lookup" in new WebBrowserWithJs {
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

    "Validate the address code only if missing " in new WebBrowserWithJs {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      widget.postCodeSearch.value = "AAAAAAA"
      click on AddressPickerPage.submit
      val errors = ErrorPanel.text.lines.filter(_ != "Please check the form").toSeq
      errors should have size 1
      errors.head should include(Messages("address-picker-1.address-postcode-lookup"))

    }

    "Lookup an address with ajax call" in new WebBrowserWithJs {
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

//      println("OPTIONS:" + widget.addressSelect.getOptions.map(_.getAttribute("value")).mkString("\n"))

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

    "show manual input only with javascript disabled" ignore new WebBrowserWithJs {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver

      widget.assertLookupInputVisible()
      widget.assertAddressInputsVisible()
      widget.assertServerErrorInvisible()
      widget.assertMissingPostcodeInvisible()
    }

    "show server error message" in new WebBrowserWithJs {
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

    "show server postcode not found message" in new WebBrowserWithJs {
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

    "validate required elements" in new WebBrowserWithJs {
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

    "preserve the submitted values" in new WebBrowserWithJs {
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

    "submit when required are present" in new WebBrowserWithJs {
      val model = Address(
        SearchFields(showSearchFields = true,
          showAddressSelect = true,
          showAddressFields = true,
          Some("AA11AA"),
          Some("1"),
          remember = true
        ),
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
      AddressPickerModel.JsonFormat.reads(Json.parse(json))
        .map(a => a.address1 should equal(model)) orElse fail("Did not have a AddressPickerModel in the response")
    }

    "enter address manually with html unit only" in new WebBrowserWithJs() {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.assertLookupInputVisible()
      click on widget.enterAddressManuallyLink

      widget.assertLookupInputInvisible()

      // just make sure the address line is visible and it's value can be entered
      widget.addressLine1.value = "address line 1"
    }

    "Enter address manually should work with javascript disabled" in new WebBrowser() {
      val model = Address(
        SearchFields(showSearchFields = false,
          showAddressSelect = false,
          showAddressFields = true,
          None,
          None,
          remember = true
        ),
        "address line 1",
        Some("address line 2"),
        Some("address line 3"),
        "Post town",
        "N19 3NN"
      )
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.addressLine1.value = model.streetAddress1
      widget.addressLine2.value = model.streetAddress2.get
      widget.addressLine3.value = model.streetAddress3.get
      widget.town.value = model.postTown
      widget.postcode.value = model.postCode
      widget.remember.select()
      click on AddressPickerPage.submit

      val addressCookie = webDriver.manage().getCookieNamed(AddressPickerModel.Key.value)
      val json = addressCookie.getValue
      AddressPickerModel.JsonFormat.reads(Json.parse(json))
        .map(a => a.address1 should equal(model)) orElse fail("Did not have a AddressPickerModel in the response")
    }

    "form validation with js disabled" in new WebBrowser() {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      click on AddressPickerPage.submit

      val errors = ErrorPanel.text.lines.filter(_ != "Please check the form").toSeq
      errors.head should include(Messages("address-picker-1.address-line-1"))
      errors.head should include(Messages("address-picker-1.post-town"))
      errors.head should include(Messages("address-picker-1.post-code"))
    }
  }
}
