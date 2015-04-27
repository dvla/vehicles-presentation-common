package uk.gov.dvla.vehicles.presentation.common.views

import org.scalatest.AppendedClues
import play.api.i18n.Messages
import play.api.libs.json.{Json, JsString}
import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness
import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec
import uk.gov.dvla.vehicles.presentation.common.model.Address
import uk.gov.dvla.vehicles.presentation.common.models.AddressPickerModel
import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, AddressPickerPage, DatePage}

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
      widget.county.value should equal("")
      widget.postcode.value should equal("")
    }

    "validate required element" in new WebBrowser {
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.addressLine2.value = "address 2"
      widget.addressLine3.value = "address 3"
      widget.county.value = "county"

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
      widget.county.value = "county"
      widget.postcode.value = "" //leave one missing to see if the others are there after submit

      click on AddressPickerPage.submit
      page.title should equal(AddressPickerPage.title)
      ErrorPanel.text should include(Messages("error.address.postCode"))
      widget.addressLine1.value should equal("address 1")
      widget.addressLine2.value should equal("address 2")
      widget.addressLine3.value should equal("address 3")
      widget.town.value should equal("town")
      widget.county.value should equal("county")
      widget.postcode.value should equal("")
    }

    "submit when required are present" in new WebBrowser {
      val model = Address(
        "address line 1",
        Some("address line 2"),
        Some("address line 3"),
        "Post town",
        Some("Orange county"),
        "N19 3NN"
      )
      go to AddressPickerPage
      val widget = AddressPickerPage.addressPickerDriver
      widget.addressLine1.value = model.streetAddress1
      widget.addressLine2.value = model.streetAddress2.get
      widget.addressLine3.value = model.streetAddress3.get
      widget.town.value = model.postTown
      widget.county.value = model.county.get
      widget.postcode.value = model.postCode
      click on AddressPickerPage.submit
      page.title should equal("Success") withClue(s"Errors: ${ErrorPanel.text}")
      val addressCookie = webDriver.manage().getCookieNamed(AddressPickerModel.Key.value)
      println("addressCookie: " + addressCookie)
//      println("val json = Json.parse(jsonString)" + Json.parse(addressCookie)\ "address1")
//      println("addressCookie:" + AddressPickerModel.JsonFormat.reads(Json.parse(addressCookie)))
//      AddressPickerModel.JsonFormat.reads(Json.parse(addressCookie)).map(a => a.address1 should equal(model)) orElse(fail("############"))
    }
  }
}
