package uk.gov.dvla.vehicles.presentation.common.views.widgetdriver

import org.openqa.selenium.{By, WebDriver}
import org.scalatest.selenium.WebBrowser
import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions}
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.AddressLine1Id
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.AddressLine2Id
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.AddressLine3Id
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.PostcodeId
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.PostTownId
import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.RememberId

class AddressPickerDriver(id: String) extends WebBrowser  {

  def postCodeSearch(implicit driver: WebDriver): TextField =
    textField(cssSelector(".js-address-postcode-lookup"))

  def searchButton(implicit driver: WebDriver): Element =
    find(id("address-find"))
      .getOrElse(throw new Exception(s"Cannot find element with id address-postcode-lookup in address picker with id:$id "))

  def enterManuallyLink(implicit driver: WebDriver): Element =
    find(cssSelector(".address-manual-toggle"))
      .getOrElse(throw new Exception(s"Cannot find element with id address-manual-toggle in address picker with id:$id "))


  def search(postcode: String)(implicit driver: WebDriver): Unit = {
    postCodeSearch.value = postcode
    click on searchButton
    Wait.until(selectPopulated)
  }

  def manualEnter()(implicit driver: WebDriver): Unit = {
    click on enterManuallyLink
    Wait.until(invisibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))
  }

  def addressSelect(implicit driver: WebDriver): SingleSel =
    singleSel(cssSelector(".js-address-list"))

  def selectAddress(value: String)(implicit driver: WebDriver): Unit = {
    addressSelect.value = value
  }

  def addressLine1(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine1Id"))

  def addressLine2(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine2Id"))

  def addressLine3(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine3Id"))

  def town(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$PostTownId"))

  def postcode(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$PostcodeId"))

  def remember(implicit driver: WebDriver): Checkbox =
    checkbox(id(s"${id}_$RememberId"))

  def assertAddressVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id")))
  }

  def assertAddressInputsVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .address-manual-inputs-wrapper")))
  }

  def assertAddressInputsInvisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .address-manual-inputs-wrapper")))
  }

  def assertAddressListVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .address-list-wrapper")))
  }

  def assertAddressListInvisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .address-list-wrapper")))
  }

  def assertLookupInputVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))
  }

  def assertLookupInputInvisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))
  }

  def assertServerErrorVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .server-message")))
  }

  def assertServerErrorInvisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .server-message")))
  }

  def assertMissingPostcodeVisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .missing-postcode")))
  }

  def assertMissingPostcodeInvisible()(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .missing-postcode")))
  }

  def enterAddressManuallyLink(implicit driver: WebDriver): Element =
    find(cssSelector(s"#$id .address-manual-toggle")) getOrElse(
      throw new Exception(s"Unable to find element with id css selector (#$id .address-manual-toggle)")
    )

  def changeMyDetailsLink(implicit driver: WebDriver): Element =
    find(cssSelector(s"#$id .address-reset-form")) getOrElse(
      throw new Exception(s"Unable to find element with css selector (#$id .address-reset-form)")
    )

  private def selectPopulated: ExpectedCondition[Boolean] = {
    new ExpectedCondition[Boolean]() {
      override def apply(driver: WebDriver): Boolean = {
        try driver.findElement(By.cssSelector(".js-address-list")).getAttribute("data-ajax") == "true"
        catch {
          case e: Throwable =>
            println(e)
            false
        }
      }
    }
  }
}
