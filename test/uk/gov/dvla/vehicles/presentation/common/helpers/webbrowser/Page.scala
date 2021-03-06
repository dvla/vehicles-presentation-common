/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * TODO Check for licensing issues as the code below is based on code found in Scalatest
 */

package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser

import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.Wait

trait Page extends org.scalatest.selenium.Page {
  val title: String

  def waitUntilJavascriptReady(implicit driver: WebDriver): Unit = {
    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".js-ready")))
  }

  def assetComponentInvisible(cssClass: String)(implicit driver: WebDriver) =
    new WebDriverWait(driver, 7)
      .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssClass)))

  def assetComponentVisible(cssClass: String)(implicit driver: WebDriver) =
    new WebDriverWait(driver, 7)
      .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(cssClass)))

  def elementHasClass(cssSelector: String, hasClass: String)(implicit driver: WebDriver): Boolean =
    driver.findElement(By.cssSelector(cssSelector)).getAttribute("class").contains(hasClass)

}
