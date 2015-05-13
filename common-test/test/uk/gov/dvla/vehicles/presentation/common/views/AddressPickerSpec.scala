package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import play.api.i18n.Messages
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, AddressPickerPage}

class AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {
  "Address picker widget" should {
    "Show all the expected fields" in new WebBrowser {
      go to AddressPickerPage
      page.title should equal(AddressPickerPage.title)

      val widget = AddressPickerPage.addressPickerDriver
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

    "validate required element" in new WebBrowser {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.addressLine2.value = "address 2"
      widget.addressLine3.value = "address 3"

      click on AddressPickerPage.submit
      page.title should equal(AddressPickerPage.title)
      ErrorPanel.text should include(Messages("error.address.addressLine1"))
      ErrorPanel.text should include(Messages("error.address.postTown"))
      ErrorPanel.text should include(Messages("error.address.postCode"))
    }

    "preserve the submitted values" in new WebBrowser {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
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

    "submit when required are present" in new WebBrowser {
      val model = Address(
        "address line 1",
        Some("address line 2"),
        Some("address line 3"),
        "Post town",
        "N19 3NN",
        remember = true
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
      page.title should equal("Success") withClue s"Errors: ${ErrorPanel.text}"
      val addressCookie = webDriver.manage().getCookieNamed(AddressPickerModel.Key.value)
      val json = addressCookie.getValue
      println("addressCookie: " + json)
      AddressPickerModel.JsonFormat.reads(Json.parse(json))
        .map(a => a.address1 should equal(model)) orElse(fail("Did not have a AddressPickerModel in the response"))
    }
  }
}
