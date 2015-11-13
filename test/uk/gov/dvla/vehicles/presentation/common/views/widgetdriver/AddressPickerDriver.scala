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
  private implicit val timeout = 3

  def postCodeSearch(implicit driver: WebDriver): TextField =
    textField(cssSelector(".js-address-postcode-lookup"))//(driver.findElement(By.id(id)))

  def searchButton(implicit driver: WebDriver): Element =
    find(id("address-find"))//(driver.findElement(By.id(id)))
      .getOrElse(throw new Exception(s"Cannot find element with id address-postcode-lookup in address picker with id:$id "))

  def enterManuallyLink(implicit driver: WebDriver): Element =
    find(cssSelector(".address-manual-toggle"))//(driver.findElement(By.id(id)))
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
    singleSel(cssSelector(".js-address-list"))   //(driver.findElement(By.id(id)))

  def selectAddress(value: String)(implicit driver: WebDriver): Unit = {
    addressSelect.value = value
  }

  def addressLine1(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine1Id"))//(driver.findElement(By.id(id)))

  def addressLine2(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine2Id"))//(driver.findElement(By.id(id)))

  def addressLine3(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$AddressLine3Id"))//(driver.findElement(By.id(id)))

  def town(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$PostTownId"))//(driver.findElement(By.id(id)))

  def postcode(implicit driver: WebDriver): TextField =
    textField(id(s"${id}_$PostcodeId"))//(driver.findElement(By.id(id)))

  def remember(implicit driver: WebDriver): Checkbox =
    checkbox(id(s"${id}_$RememberId"))//(driver.findElement(By.id(id)))

  def assertAddressVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id")))
  }

  def assertAddressInputsVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .address-manual-inputs-wrapper")))
  }

  def assertAddressInputsInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .address-manual-inputs-wrapper")))
  }

  def assertAddressListVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .address-list-wrapper")))
  }

  def assertAddressListInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .address-list-wrapper")))
  }

  def assertLookupInputVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))
  }

  def assertLookupInputInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))
  }

  def assertServerErrorVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .server-message")))
  }

  def assertServerErrorInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .server-message")))
  }

  def assertMissingPostcodeVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .missing-postcode")))
  }

  def assertMissingPostcodeInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {
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
