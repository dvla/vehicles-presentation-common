package uk.gov.dvla.vehicles.presentation.common.views.widgetdriver

import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{By, WebDriver}
import org.scalatest.selenium.WebBrowser._
import uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.Wait.elementHasAnyText
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{SingleSel, Element, WebBrowserDSL, TextField}
import org.openqa.selenium.support.ui.ExpectedConditions.{elementToBeSelected, invisibilityOfElementLocated}

class AddressPickerDriver(id: String)  extends WebBrowserDSL {
  def postCodeSearch(implicit driver: WebDriver): TextField =
    textField(id("address-postcode-lookup"))(driver.findElement(By.id(id)))

  def searchButton(implicit driver: WebDriver): Element =
    find(id("address-postcode-lookup"))(driver.findElement(By.id(id)))
      .getOrElse(throw new Exception(s"Cannot find element with id address-postcode-lookup in address picker with id:$id "))

  def enterManuallyLink(implicit driver: WebDriver): Element =
    find(cssSelector(".address-manual-toggle"))(driver.findElement(By.id(id)))
      .getOrElse(throw new Exception(s"Cannot find element with id address-manual-toggle in address picker with id:$id "))

  def search(postcode: String)(implicit driver: WebDriver): Unit = {
    postCodeSearch.value = postcode
    Wait.until(elementToBeSelected(By.cssSelector((s"#$id #address-list"))))
  }

  def manualEnter()(implicit driver: WebDriver): Unit = {
    click on enterManuallyLink
    Wait.until(invisibilityOfElementLocated(By.cssSelector((s"#$id .postcode-lookup-container"))))
  }

  def addressSelect(implicit driver: WebDriver): SingleSel =
    singleSel(id("address-list"))(driver.findElement(By.id(id)))

  def selectAddress(value: String)(implicit driver: WebDriver): Unit = {
    addressSelect.value = value
    Wait.until(elementHasAnyText(By.cssSelector((s"#$id #addressLines-1"))))
  }

  def addressLine1(implicit driver: WebDriver): TextField =
    textField(id("addressLines-1"))(driver.findElement(By.id(id)))

  def addressLine2(implicit driver: WebDriver): TextField =
    textField(id("addressLines-2"))(driver.findElement(By.id(id)))

  def addressLine3(implicit driver: WebDriver): TextField =
    textField(id("addressLines-3"))(driver.findElement(By.id(id)))

  def town(implicit driver: WebDriver): TextField =
    textField(id("address-town"))(driver.findElement(By.id(id)))

  def county(implicit driver: WebDriver): TextField =
    textField(id("address-county"))(driver.findElement(By.id(id)))

  def postcode(implicit driver: WebDriver): TextField =
    textField(id("address-postcode"))(driver.findElement(By.id(id)))
}
