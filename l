[1mdiff --git a/app/assets/javascripts/page-init.js b/app/assets/javascripts/page-init.js[m
[1mindex 74d6f27..f6b61f2 100644[m
[1m--- a/app/assets/javascripts/page-init.js[m
[1m+++ b/app/assets/javascripts/page-init.js[m
[36m@@ -142,7 +142,8 @@[m [mdefine(function(require) {[m
         // Last minute available at T minus 1[m
         var lastMinuteInMinutes = closingTimeInMinutes - 1;[m
 [m
[31m-        if (currentTimeInMinutes >= warningInMinutes && currentTimeInMinutes <= lastMinuteInMinutes) {            var refreshTimer = setInterval(function () {[m
[32m+[m[32m        if (currentTimeInMinutes >= warningInMinutes && currentTimeInMinutes <= lastMinuteInMinutes) {[m
[32m+[m[32m            var refreshTimer = setInterval(function () {[m
                 dClosing = new Date();[m
                 dCurrentTimeInMinutes = dClosing.getHours() * 60 + dClosing.getMinutes();[m
                 minLeft = lastMinuteInMinutes - dCurrentTimeInMinutes;[m
[36m@@ -444,6 +445,9 @@[m [mdefine(function(require) {[m
                     return true;[m
                 });[m
                 addressLookup.enableAddressLookup()[m
[32m+[m
[32m+[m[32m                // Last command adds class, so we have a condition to wait for when unit-testing[m
[32m+[m[32m                $('html').addClass('js-ready');[m
             });[m
         }[m
     };[m
[1mdiff --git a/app/assets/scss/base/_template.scss b/app/assets/scss/base/_template.scss[m
[1mindex dea788f..24dd788 100644[m
[1m--- a/app/assets/scss/base/_template.scss[m
[1m+++ b/app/assets/scss/base/_template.scss[m
[36m@@ -304,7 +304,7 @@[m [mimg {[m
         white-space: nowrap;[m
         min-width: 92px;[m
         width: auto !important;[m
[31m-        display: inline-block !important;[m
[32m+[m[32m        display: inline-block;[m
         &:focus {[m
             background-color: inherit;[m
             outline: inherit;[m
[1mdiff --git a/app/assets/scss/components/_form.scss b/app/assets/scss/components/_form.scss[m
[1mindex 3df31bb..f47a25c 100644[m
[1m--- a/app/assets/scss/components/_form.scss[m
[1m+++ b/app/assets/scss/components/_form.scss[m
[36m@@ -3,7 +3,7 @@[m
 #{$htmlNamespace} {[m
 [m
     //Hides .js-only if .js-enabled not applied to <body>[m
[31m-    body:not(.js-enabled) &  .js-only {[m
[32m+[m[32m    body:not(.js-enabled) & .js-only, body.js-disabled .js-only {[m
         display:none;[m
     }[m
 [m
[36m@@ -12,6 +12,10 @@[m
         display:none;[m
     }[m
 [m
[32m+[m[32m    .no-js-hidden {[m
[32m+[m[32m        display: none;[m
[32m+[m[32m    }[m
[32m+[m
     fieldset {[m
         border: none;[m
         padding: 0;[m
[1mdiff --git a/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/base/valtechDeclareCheck.scala.html b/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/base/valtechDeclareCheck.scala.html[m
[1mindex 1002b24..04bb8d6 100644[m
[1m--- a/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/base/valtechDeclareCheck.scala.html[m
[1m+++ b/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/base/valtechDeclareCheck.scala.html[m
[36m@@ -24,7 +24,7 @@[m
         >[m
         @valtechLabelText(field,args)[m
         @if(args.contains('tooltipText)) {[m
[31m-            <a href="?#@id" tabindex="-1" class="field-help js-only" data-tooltip="tooltip_@id" aria-hidden="true">[m
[32m+[m[32m            <a href="?#@id" class="field-help js-only" data-tooltip="tooltip_@id" aria-hidden="true">[m
                 <span class="field-help-icon">i</span>[m
                 <span class="field-help-close" >Close</span>[m
             </a>[m
[1mdiff --git a/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/optionalToggle.scala.html b/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/optionalToggle.scala.html[m
[1mindex 0795949..f023610 100644[m
[1m--- a/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/optionalToggle.scala.html[m
[1m+++ b/app/uk/gov/dvla/vehicles/presentation/common/views/widgets/optionalToggle.scala.html[m
[36m@@ -8,7 +8,7 @@[m
 @import _root_.views.html.helper.input[m
 [m
 @input(field) { (id, name, value, htmlArgs) =>[m
[31m-<div class="expandable-optional" id="@id">[m
[32m+[m[32m<div class="expandable-optional" id="@id-group">[m
     <div class="form-item">[m
         <fieldset>[m
             <legend>@title</legend>[m
[1mdiff --git a/common-test/app/uk/gov/dvla/vehicles/presentation/common/views/govMain.scala.html b/common-test/app/uk/gov/dvla/vehicles/presentation/common/views/govMain.scala.html[m
[1mindex 39ac3bf..76f6736 100644[m
[1m--- a/common-test/app/uk/gov/dvla/vehicles/presentation/common/views/govMain.scala.html[m
[1m+++ b/common-test/app/uk/gov/dvla/vehicles/presentation/common/views/govMain.scala.html[m
[36m@@ -31,10 +31,9 @@[m
         var _gaq = _gaq || [];[m
     </script>[m
 </head>[m
[31m-<body>[m
[31m-[m
[32m+[m[32m<body class="js-disabled">[m
 <script type="text/javascript">[m
[31m-    document.body.className = ((document.body.className) ? document.body.className + ' js-enabled' : 'js-enabled');[m
[32m+[m[32m    document.body.className = document.body.className.replace('js-disabled','js-enabled');[m
 </script>[m
 <a href="#main" class="visuallyhidden">Skip to main content</a>[m
 [m
[1mdiff --git a/common-test/conf/messages b/common-test/conf/messages[m
[1mindex 502ffec..6cfbd41 100644[m
[1m--- a/common-test/conf/messages[m
[1m+++ b/common-test/conf/messages[m
[36m@@ -351,4 +351,7 @@[m [mglobal-please-wait-close = Close this message[m
 service-closing-warning.title = Service Closing Warning[m
 global.serviceCloseWarning = This service is available from {0} to {1}, you have[m
 global.serviceCloseWarning.p2 = to complete this service.[m
[31m-global.serviceClosed = Service now closed. Service is available {0} to {1}[m
\ No newline at end of file[m
[32m+[m[32mglobal.serviceClosed = Service now closed. Service is available {0} to {1}[m
[32m+[m
[32m+[m[32m#responsive-utilities[m
[32m+[m[32mresponsive-utilities.title = Responsive Utilities[m
\ No newline at end of file[m
[1mdiff --git a/common-test/conf/routes b/common-test/conf/routes[m
[1mindex f3b414e..8b08b01 100644[m
[1m--- a/common-test/conf/routes[m
[1m+++ b/common-test/conf/routes[m
[36m@@ -96,3 +96,5 @@[m [mPOST    /adress-and-postcode            @uk.gov.dvla.vehicles.presentation.commo[m
 GET     /please-wait                    @uk.gov.dvla.vehicles.presentation.common.controllers.PleaseWaitController.present[m
 [m
 GET     /service-closing-warning        @uk.gov.dvla.vehicles.presentation.common.controllers.ServiceClosingWarningController.present[m
[32m+[m
[32m+[m[32mGET     /responsive-utilities           @uk.gov.dvla.vehicles.presentation.common.controllers.ResponsiveUtilitiesController.present[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/AddressPickerPage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/AddressPickerPage.scala[m
[1mindex 711112d..5390e57 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/AddressPickerPage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/AddressPickerPage.scala[m
[36m@@ -1,6 +1,6 @@[m
 package uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
[31m-import org.openqa.selenium.SearchContext[m
[32m+[m[32mimport org.openqa.selenium.{WebDriver}[m
 import org.scalatest.selenium.WebBrowser.Element[m
 import org.scalatest.selenium.WebBrowser.find[m
 import org.scalatest.selenium.WebBrowser.id[m
[36m@@ -13,9 +13,9 @@[m [mobject AddressPickerPage  extends Page {[m
   override val url: String = WebDriverFactory.testUrl + address.substring(1)[m
   final override val title: String = "Address Picker"[m
   val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address[m
[31m-  implicit lazy val webDriver = WebDriverFactory.webDriver[m
 [m
   val addressPickerDriver = new AddressPickerDriver(AddressPickerModel.Form.datePicker1Id)[m
 [m
[31m-  def submit(implicit driver: SearchContext): Element = find(id("submit")).get[m
[32m+[m[32m  def submit(implicit driver: WebDriver): Element = find(id("submit")).get[m
[32m+[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DateOfSalePage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DateOfSalePage.scala[m
[1mindex 120ee34..bb053f1 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DateOfSalePage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DateOfSalePage.scala[m
[36m@@ -2,17 +2,10 @@[m [mpackage uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
 import uk.gov.dvla.vehicles.presentation.common.helpers[m
 import uk.gov.dvla.vehicles.presentation.common.models[m
[31m-[m
[31m-import helpers.webbrowser.{Page, WebDriverFactory, _}[m
[32m+[m[32mimport helpers.webbrowser.{Page, WebDriverFactory}[m
 import models.DateOfSaleModel.Form.{OptionalDateId, DateId}[m
 import org.openqa.selenium.WebDriver[m
[31m-import org.scalatest.selenium.WebBrowser.Element[m
[31m-import org.scalatest.selenium.WebBrowser.find[m
[31m-import org.scalatest.selenium.WebBrowser.id[m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[31m-import org.scalatest.selenium.WebBrowser.click[m
[31m-import org.scalatest.selenium.WebBrowser.telField[m
[31m-import org.scalatest.selenium.WebBrowser.TelField[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
 [m
 class DateOfSalePage(implicit driver: WebDriver) extends Page {[m
 [m
[36m@@ -41,10 +34,18 @@[m [mclass DateOfSalePage(implicit driver: WebDriver) extends Page {[m
 [m
     click on submit[m
   }[m
[32m+[m
[32m+[m
 }[m
 [m
 object DateOfSalePage {[m
   def instance(implicit driver: WebDriver) = new DateOfSalePage[m
[32m+[m
[32m+[m
[32m+[m[32m  def useTodaysDateButton(implicit driver: WebDriver): Element = {[m
[32m+[m[32m    // This is the element / dom node that must be clicked to show / hide content[m
[32m+[m[32m    find(cssSelector("#todays_date")) getOrElse(throw new Exception("Unable to find help icon "))[m
[32m+[m[32m  }[m
 }[m
 [m
 [m
[36m@@ -58,6 +59,7 @@[m [mclass DateOfSaleWidget(idStr: String)(implicit driver: WebDriver) {[m
   lazy val month: TelField = telField(id(s"${idStr}_month"))[m
 [m
   lazy val year: TelField = telField(id(s"${idStr}_year"))[m
[32m+[m
 }[m
 [m
 object DateOfSaleWidget {[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DatePage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DatePage.scala[m
[1mindex 377a2fa..59fcf7d 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DatePage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/DatePage.scala[m
[36m@@ -1,6 +1,6 @@[m
 package uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
[31m-import org.openqa.selenium.{SearchContext, WebDriver}[m
[32m+[m[32mimport org.openqa.selenium.{WebDriver}[m
 import org.scalatest.selenium.WebBrowser.Element[m
 import org.scalatest.selenium.WebBrowser.find[m
 import org.scalatest.selenium.WebBrowser.id[m
[36m@@ -12,7 +12,7 @@[m [mimport uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory[m
 import uk.gov.dvla.vehicles.presentation.common.models.DateModel.Form.{DateId, OptionalDateId}[m
 [m
[31m-class DatePage(implicit driver: SearchContext) extends Page {[m
[32m+[m[32mclass DatePage(implicit driver: WebDriver) extends Page {[m
 [m
   final val address = "/date"[m
   override val url: String = WebDriverFactory.testUrl + address.substring(1)[m
[36m@@ -22,8 +22,8 @@[m [mclass DatePage(implicit driver: SearchContext) extends Page {[m
 [m
   lazy val required: DateWidget = DateWidget(DateId)[m
 [m
[31m-  def submit(implicit driver: SearchContext): Element = {[m
[31m-    implicit lazy val webDriver = WebDriverFactory.webDriver[m
[32m+[m[32m  def submit(implicit driver: WebDriver): Element = {[m
[32m+[m[32m//    implicit lazy val webDriver = WebDriverFactory.webDriver[m
     find(id("submit")).get[m
   }[m
 [m
[36m@@ -45,12 +45,12 @@[m [mclass DatePage(implicit driver: SearchContext) extends Page {[m
 }[m
 [m
 object DatePage {[m
[31m-  def instance(implicit driver: SearchContext) = new DatePage[m
[32m+[m[32m  def instance(implicit driver: WebDriver) = new DatePage[m
 }[m
 [m
 [m
[31m-class DateWidget(idStr: String)(implicit driver: SearchContext) {[m
[31m-  implicit lazy val webDriver = WebDriverFactory.webDriver[m
[32m+[m[32mclass DateWidget(idStr: String)(implicit driver: WebDriver) {[m
[32m+[m[32m//  implicit lazy val webDriver = WebDriverFactory.webDriver[m
 [m
   def label: String = ???[m
 [m
[36m@@ -66,5 +66,5 @@[m [mclass DateWidget(idStr: String)(implicit driver: SearchContext) {[m
 }[m
 [m
 object DateWidget {[m
[31m-  def apply(idStr: String)(implicit driver: SearchContext): DateWidget = new DateWidget(idStr)[m
[32m+[m[32m  def apply(idStr: String)(implicit driver: WebDriver): DateWidget = new DateWidget(idStr)[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/FeedbackFormPage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/FeedbackFormPage.scala[m
[1mindex 4c5c51c..a39e88f 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/FeedbackFormPage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/FeedbackFormPage.scala[m
[36m@@ -1,13 +1,7 @@[m
 package uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
 import org.openqa.selenium.WebDriver[m
[31m-import org.scalatest.selenium.WebBrowser.click[m
[31m-import org.scalatest.selenium.WebBrowser.Element[m
[31m-import org.scalatest.selenium.WebBrowser.find[m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[31m-import org.scalatest.selenium.WebBrowser.id[m
[31m-import org.scalatest.selenium.WebBrowser.textArea[m
[31m-import org.scalatest.selenium.WebBrowser.TextArea[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory[m
 import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm.Form.feedback[m
[36m@@ -27,4 +21,7 @@[m [mobject FeedbackFormPage extends Page {[m
     feedbackElement.value = feedback[m
     click on submit[m
   }[m
[32m+[m
[32m+[m[32m  def feedbackCounterElement(implicit driver: WebDriver): Element =[m
[32m+[m[32m    find(cssSelector(".character-countdown")) getOrElse(throw new Exception("Unable to find character counter "))[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/OptionTogglePage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/OptionTogglePage.scala[m
[1mindex ffc7cc1..ad0a55a 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/OptionTogglePage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/OptionTogglePage.scala[m
[36m@@ -1,36 +1,36 @@[m
 package uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
 import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}[m
[31m-import org.openqa.selenium.{WebDriver, By, SearchContext}[m
[31m-import org.scalatest.selenium.WebBrowser.Element[m
[31m-import org.scalatest.selenium.WebBrowser.find[m
[31m-import org.scalatest.selenium.WebBrowser.id[m
[31m-import org.scalatest.selenium.WebBrowser.RadioButtonGroup[m
[31m-import org.scalatest.selenium.WebBrowser.tagName[m
[31m-import org.scalatest.selenium.WebBrowser.TextField[m
[31m-import org.scalatest.selenium.WebBrowser.textField[m
[31m-import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page[m
[31m-import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory[m
[31m-import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.OptionFieldSuffix[m
[31m-import uk.gov.dvla.vehicles.presentation.common.models.OptionalToggleModel.Form._[m
[32m+[m[32mimport uk.gov.dvla.vehicles.presentation.common.helpers[m
[32m+[m[32mimport helpers.webbrowser.{WebDriverFactory, Page}[m
[32m+[m[32mimport org.openqa.selenium.{WebElement, By, WebDriver}[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
[32m+[m[32mimport uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.{OptionFieldSuffix}[m
[32m+[m[32mimport scala.collection.JavaConverters._[m
 [m
 object OptionTogglePage extends Page {[m
[32m+[m
   final val address = "/option-toggle"[m
[31m-  override val url: String = WebDriverFactory.testUrl + address.substring(1)[m
[31m-  final override val title: String = "Option Toggle"[m
[32m+[m[32m  override val url: String = WebDriverFactory.testUrl + address[m
   val jsTestUrl = WebDriverFactory.testUrl + "jstest" + address[m
[31m-  implicit lazy val webDriver = WebDriverFactory.webDriver[m
 [m
[31m-  class OptionToggleWidget[T](widgetId: String, t: Element => T)[m
[31m-                             (driver: SearchContext) {[m
[31m-    private implicit def widget = driver.findElement(By.id(widgetId))[m
[32m+[m[32m  final override val title: String = "Option Toggle"[m
[32m+[m
[32m+[m[32m  class OptionToggleWidget[T](widgetId: String, t: Element => T)(implicit driver: WebDriver) {[m
[32m+[m[32m    private implicit def widget = driver.findElement(By.id(widgetId.concat("-group")))[m
[32m+[m[32m    private def inputRadioWithMatchingValue(webElement: WebElement, value: String): Boolean =[m
[32m+[m[32m      webElement.getTagName.toLowerCase == "input" &&[m
[32m+[m[32m        webElement.getAttribute("type").toLowerCase == "radio" &&[m
[32m+[m[32m        webElement.getAttribute("value") == value[m
 [m
[31m-    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, WebDriverFactory.webDriver)[m
[32m+[m[32m    def label = widget.findElement(By.tagName("legend"))[m
[32m+[m[32m    def radio: RadioButtonGroup = new RadioButtonGroup(widgetId, driver)[m
 [m
[31m-    def label: Element = find(tagName("legend"))//(widget)[m
[31m-      .getOrElse(throw new Exception(s"Cannot find name(legend element) for OptionToggleWidget with id:$widgetId"))[m
[32m+[m[32m    def radioInputWithValue(value: String) = {[m
[32m+[m[32m      widget.findElements(By.name(widgetId)).asScala.toList.filter(inputRadioWithMatchingValue(_,value)).head[m
[32m+[m[32m    }[m
 [m
[31m-    def component(implicit driver: SearchContext): T = t(find(id(widgetId + OptionFieldSuffix)).getOrElse([m
[32m+[m[32m    def component(implicit driver: WebDriver): T = t(find(id(widgetId + OptionFieldSuffix)).getOrElse([m
       throw new Exception(s"Cannot find component element withId: $widgetId$OptionFieldSuffix " +[m
         s"for OptionToggleWidget with id:$widgetId"))[m
     )[m
[36m@@ -44,44 +44,16 @@[m [mobject OptionTogglePage extends Page {[m
         .until(ExpectedConditions.visibilityOfElementLocated(By.id(widgetId + OptionFieldSuffix)))[m
   }[m
 [m
[31m-//  def textRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField]([m
[31m-//    OptionalStringOptionId,[m
[31m-//    optionalDiv => TextField([m
[31m-//      find(id(OptionalStringId))(WebDriverFactory.webDriver).getOrElse([m
[31m-//        throw new Exception(s"Cannot find component element withId: $OptionalStringId"))[m
[31m-//        .underlying[m
[31m-//    )[m
[31m-//  )(driver)[m
[31m-[m
[31m-  def textRadio(implicit driver: SearchContext) = {[m
[31m-    println("---------")[m
[31m-    println (">>>> " + OptionalStringId)[m
[31m-    println (">>>> " + id(OptionalStringId))[m
[31m-    println (">>>> " + find(id(OptionalStringId)))[m
[31m-    println("---------")[m
[31m-    new OptionToggleWidget[TextField]([m
[31m-    OptionalStringOptionId,[m
[31m-    optionalDiv => textField(id(OptionalStringId))[m
[31m-  )(driver)[m
[31m-  }[m
[31m-[m
[31m-//  def intRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField]([m
[31m-//    OptionalIntOptionId,[m
[31m-//    optionalDiv => TextField(find(id(OptionalIntId))(WebDriverFactory.webDriver).getOrElse([m
[31m-//      throw new Exception(s"Cannot find component element withId: $OptionalIntId"))[m
[31m-//      .underlying[m
[31m-//    )[m
[31m-//  )(driver)[m
[32m+[m[32m  def textRadioOption(implicit driver: WebDriver) =[m
[32m+[m[32m    new OptionToggleWidget[TextField]("optional-string-option", optionalDiv => textField(id("optional-string")))[m
 [m
[31m-  def intRadio(implicit driver: SearchContext) = new OptionToggleWidget[TextField]([m
[31m-    OptionalIntOptionId,[m
[31m-    optionalDiv => textField(id(OptionalStringId))[m
[31m-  )(driver)[m
[32m+[m[32m  def intRadioOption(implicit driver: WebDriver) =[m
[32m+[m[32m    new OptionToggleWidget[TextField]("optional-int-option", optionalDiv => textField(id("optional-int")))[m
 [m
[31m-  def dateRadio(implicit driver: SearchContext) =[m
[31m-    new OptionToggleWidget[DateWidget](OptionalDateOptionId, _ => DateWidget(OptionalDateId))(driver)[m
[32m+[m[32m  def dateRadioOption(implicit driver: WebDriver) =[m
[32m+[m[32m    new OptionToggleWidget[DateWidget]("optional-date-option", _ => DateWidget("optional-date"))[m
 [m
[31m-  def submit(implicit driver: SearchContext): Element =  find(id("submit")).getOrElse {[m
[32m+[m[32m  def submit(implicit driver: WebDriver): Element = find(id("submit")).getOrElse {[m
     throw new Exception(s"Cannot find submit on the page")[m
   }[m
[31m-}[m
[32m+[m[32m}[m
\ No newline at end of file[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/SummaryWrapperPage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/SummaryWrapperPage.scala[m
[1mindex ba1facd..3e20121 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/SummaryWrapperPage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/SummaryWrapperPage.scala[m
[36m@@ -12,15 +12,14 @@[m [mobject SummaryWrapperPage extends Page {[m
   override val url: String = WebDriverFactory.testUrl + address.substring(1)[m
   final override val title: String = "Summary-Wrapper"[m
 [m
[31m-[m
[31m-  def contentElement()(implicit driver: WebDriver): Element = {[m
[31m-    // This is the wrapper / dom node that contains the hidden content[m
[31m-    find(cssSelector(".details")) getOrElse(throw new Exception("Could not find content element"))[m
[31m-  }[m
[32m+[m[32m  final val contentCssSelector = ".details"[m
[32m+[m[32m  final val showHideTriggerCssSelector = ".summary"[m
 [m
   def showHideTriggerElement()(implicit driver: WebDriver): Element = {[m
     // This is the element / dom node that must be clicked to show / hide content[m
[31m-    find(cssSelector(".summary")) getOrElse(throw new Exception(s"Unable to find element for show/hide content"))[m
[32m+[m[32m    find(cssSelector(showHideTriggerCssSelector)) getOrElse([m
[32m+[m[32m      throw new Exception(s"Unable to find element for show/hide content")[m
[32m+[m[32m      )[m
   }[m
 [m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/V5cRegistrationNumberPage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/V5cRegistrationNumberPage.scala[m
[1mindex 1348547..66f1960 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/V5cRegistrationNumberPage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/V5cRegistrationNumberPage.scala[m
[36m@@ -1,13 +1,7 @@[m
 package uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
 import org.openqa.selenium.WebDriver[m
[31m-import org.scalatest.selenium.WebBrowser.TextField[m
[31m-import org.scalatest.selenium.WebBrowser.textField[m
[31m-import org.scalatest.selenium.WebBrowser.Element[m
[31m-import org.scalatest.selenium.WebBrowser.find[m
[31m-import org.scalatest.selenium.WebBrowser.id[m
[31m-import org.scalatest.selenium.WebBrowser.click[m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.Page[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory[m
 import uk.gov.dvla.vehicles.presentation.common.models.V5cRegistrationNumberModel.Form.v5cRegistrationNumberID[m
[36m@@ -26,4 +20,14 @@[m [mobject V5cRegistrationNumberPage extends Page {[m
     registrationNumber.value = v5cRegistrationNumber[m
     click on submit[m
   }[m
[32m+[m
[32m+[m[32m  def helpIcon()(implicit driver: WebDriver): Element = {[m
[32m+[m[32m    // This is the element / dom node that must be clicked to show / hide content[m
[32m+[m[32m    find(cssSelector(".field-help")) getOrElse(throw new Exception("Unable to find help icon "))[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  def helpContent()(implicit driver: WebDriver): Element = {[m
[32m+[m[32m    // This is the element / dom node that must be clicked to show / hide content[m
[32m+[m[32m    find(cssSelector(".field-help-content")) getOrElse(throw new Exception("Unable to find help content"))[m
[32m+[m[32m  }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/ValtechDeclareCheckPage.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/ValtechDeclareCheckPage.scala[m
[1mindex 9887e5f..7e76d3b 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/ValtechDeclareCheckPage.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/pages/ValtechDeclareCheckPage.scala[m
[36m@@ -2,7 +2,6 @@[m [mpackage uk.gov.dvla.vehicles.presentation.common.pages[m
 [m
 import uk.gov.dvla.vehicles.presentation.common.helpers[m
 import uk.gov.dvla.vehicles.presentation.common.models[m
[31m-[m
 import helpers.webbrowser.{WebDriverFactory, Page}[m
 import models.ValtechDeclareCheckModel.Form.DeclareSelectId[m
 import org.openqa.selenium.WebDriver[m
[36m@@ -13,6 +12,7 @@[m [mimport org.scalatest.selenium.WebBrowser.Element[m
 import org.scalatest.selenium.WebBrowser.find[m
 import org.scalatest.selenium.WebBrowser.go[m
 import org.scalatest.selenium.WebBrowser.click[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
 [m
 object ValtechDeclareCheckPage extends Page {[m
 [m
[36m@@ -34,4 +34,14 @@[m [mobject ValtechDeclareCheckPage extends Page {[m
     go to ValtechDeclareCheckPage[m
     click on submit[m
   }[m
[32m+[m
[32m+[m[32m  def helpIcon()(implicit driver: WebDriver): Element = {[m
[32m+[m[32m    // This is the element / dom node that must be clicked to show / hide content[m
[32m+[m[32m    find(cssSelector(".field-help")) getOrElse(throw new Exception("Unable to find help icon "))[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  def helpContent()(implicit driver: WebDriver): Element = {[m
[32m+[m[32m    // This is the element / dom node that must be clicked to show / hide content[m
[32m+[m[32m    find(cssSelector(".field-help-content")) getOrElse(throw new Exception("Unable to find help content"))[m
[32m+[m[32m  }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/AddressPickerSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/AddressPickerSpec.scala[m
[1mindex b71bbd4..8b0ee3d 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/AddressPickerSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/AddressPickerSpec.scala[m
[36m@@ -61,7 +61,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeInvisible()[m
     }[m
 [m
[31m-    "Keep Lookup container and Dropdown select invisible on resubmit for manual lookup" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "Keep Lookup container and Dropdown select invisible on resubmit for manual lookup" in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       pageTitle should equal(AddressPickerPage.title)[m
 [m
[36m@@ -82,7 +82,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeInvisible()[m
     }[m
 [m
[31m-    "Validate the address code only if missing " ignore new WebBrowserWithJs {[m
[32m+[m[32m    "Validate the address code only if missing " in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       pageTitle should equal(AddressPickerPage.title)[m
 [m
[36m@@ -90,10 +90,9 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertLookupInputVisible()[m
       widget.postCodeSearch.value = "AAAAAAA"[m
       click on AddressPickerPage.submit[m
[31m-      val errors = ErrorPanel.text.lines.filter(_ != "Please check the form").toSeq[m
[32m+[m[32m      val errors = ErrorPanel.text.lines.filter(_ != "Please correct the details below").toSeq[m
       errors should have size 1[m
       errors.head should include(Messages("address-picker-1.address-postcode-lookup"))[m
[31m-[m
     }[m
 [m
     "Lookup an address with ajax call" in new WebBrowserWithJs {[m
[36m@@ -139,16 +138,24 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeInvisible()[m
     }[m
 [m
[31m-    "show manual input only with javascript disabled" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "show manual input only with javascript disabled" in new WebBrowserWithJs {[m
[32m+[m[32m      go to AddressPickerPage[m
[32m+[m[32m      pageTitle should equal(AddressPickerPage.title)[m
[32m+[m
[32m+[m[32m      // Testing for utility classes as our browser automation suite does not work with Javascript disabled[m
[32m+[m[32m      val widget = AddressPickerPage.addressPickerDriver[m
[32m+[m[32m      AddressPickerPage.elementHasClass(".postcode-lookup-container","no-js-hidden") should equal(true)[m
[32m+[m
[32m+[m[32m    }[m
[32m+[m[32m    "dont show manual input with javascript enabled" in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       pageTitle should equal(AddressPickerPage.title)[m
 [m
       val widget = AddressPickerPage.addressPickerDriver[m
[32m+[m[32m      widget.assertAddressVisible()[m
 [m
       widget.assertLookupInputVisible()[m
[31m-      widget.assertAddressInputsVisible()[m
[31m-      widget.assertServerErrorInvisible()[m
[31m-      widget.assertMissingPostcodeInvisible()[m
[32m+[m[32m      widget.assertAddressInputsInvisible()[m
     }[m
 [m
     "show server error message" in new WebBrowserWithJs {[m
[36m@@ -167,7 +174,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeInvisible()[m
     }[m
 [m
[31m-    "show server postcode not found message" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "show server postcode not found message" in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       pageTitle should equal(AddressPickerPage.title)[m
 [m
[36m@@ -183,7 +190,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeVisible()[m
     }[m
 [m
[31m-    "validate required elements" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "validate required elements" in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       val widget = AddressPickerPage.addressPickerDriver[m
       widget.assertLookupInputVisible()[m
[36m@@ -210,7 +217,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.assertMissingPostcodeInvisible()[m
     }[m
 [m
[31m-    "preserve the submitted values" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "preserve the submitted values" in new WebBrowserWithJs {[m
       go to AddressPickerPage[m
       val widget = AddressPickerPage.addressPickerDriver[m
       widget.assertLookupInputVisible()[m
[36m@@ -232,7 +239,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.postcode.value should equal("")[m
     }[m
 [m
[31m-    "submit when required are present" ignore new WebBrowserWithJs {[m
[32m+[m[32m    "submit when required are present" in new WebBrowserWithJs {[m
       val model = Address([m
         SearchFields(showSearchFields = true,[m
           showAddressSelect = true,[m
[36m@@ -266,7 +273,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
         .map(a => a.address1 should equal(model)) orElse fail("Did not have a AddressPickerModel in the response")[m
     }[m
 [m
[31m-    "enter address manually with html unit only" ignore new WebBrowserWithJs() {[m
[32m+[m[32m    "enter address manually with html unit only" in new WebBrowserWithJs() {[m
       go to AddressPickerPage[m
       val widget = AddressPickerPage.addressPickerDriver[m
       widget.assertLookupInputVisible()[m
[36m@@ -278,7 +285,7 @@[m [mclass AddressPickerSpec extends UiSpec with TestHarness with AppendedClues {[m
       widget.addressLine1.value = "address line 1"[m
     }[m
 [m
[31m-    "Enter address manually should work with javascript disabled" ignore new WebBrowserForSelenium {[m
[32m+[m[32m    "Enter address manually should work with javascript disabled" in new WebBrowserForSelenium {[m
       val model = Address([m
         SearchFields(showSearchFields = false,[m
           showAddressSelect = false,[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/DateOfSaleIntegrationSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/DateOfSaleIntegrationSpec.scala[m
[1mindex 84f5e8b..cab7bab 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/DateOfSaleIntegrationSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/DateOfSaleIntegrationSpec.scala[m
[36m@@ -2,8 +2,8 @@[m [mpackage uk.gov.dvla.vehicles.presentation.common.views[m
 [m
 import com.github.nscala_time.time.Imports.LocalDate[m
 import org.joda.time.chrono.ISOChronology[m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[31m-import org.scalatest.selenium.WebBrowser.pageTitle[m
[32m+[m[32mimport org.openqa.selenium.interactions.SendKeysAction[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser._[m
 import play.api.i18n.Messages[m
 import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec[m
[36m@@ -55,9 +55,6 @@[m [mclass DateOfSaleIntegrationSpec extends UiSpec with TestHarness {[m
 [m
       DateOfSalePage.instance.navigate((day + 1).toString, month.toString, year.toString)[m
 [m
[31m-[m
[31m-[m
[31m-//      ErrorPanel.text should include(Messages("error.date.inTheFuture"))[m
       ErrorPanel.numberOfErrors should equal(1)[m
     }[m
 [m
[36m@@ -71,6 +68,7 @@[m [mclass DateOfSaleIntegrationSpec extends UiSpec with TestHarness {[m
       val today = LocalDate.today[m
       success(today.toString("dd"), today.toString("MM"), today.toString("YYYY"))[m
     }[m
[32m+[m
   }[m
 [m
   "Required date of birth" should {[m
[36m@@ -79,5 +77,33 @@[m [mclass DateOfSaleIntegrationSpec extends UiSpec with TestHarness {[m
       ErrorPanel.text should include(Messages("error.date.invalid"))[m
       ErrorPanel.numberOfErrors should equal(1)[m
     }[m
[32m+[m
[32m+[m[32m    "set today's date if the button is clicked" in new WebBrowserWithJs  {[m
[32m+[m[32m      go to DateOfSalePage.instance[m
[32m+[m
[32m+[m[32m      val today = LocalDate.today[m
[32m+[m
[32m+[m[32m      DateOfSalePage.instance.waitUntilJavascriptReady[m
[32m+[m[32m      click on DateOfSalePage.useTodaysDateButton[m
[32m+[m
[32m+[m[32m      DateOfSalePage.instance.required.day.value should equal(today.toString("dd"))[m
[32m+[m[32m      DateOfSalePage.instance.required.month.value should equal(today.toString("MM"))[m
[32m+[m[32m      DateOfSalePage.instance.required.year.value should equal(today.toString("YYYY"))[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "only allow numbers to be typed in" in new WebBrowserWithJs {[m
[32m+[m[32m      val alphaChars = "#$%&'()*+,-./:;<=>?@[\\]^{|}~ \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"[m
[32m+[m[32m      val today = LocalDate.today[m
[32m+[m[32m      go to DateOfSalePage.instance[m
[32m+[m[32m      DateOfSalePage.instance.waitUntilJavascriptReady[m
[32m+[m
[32m+[m[32m      DateOfSalePage.instance.required.day.underlying.sendKeys(alphaChars.concat(today.toString("dd")))[m
[32m+[m[32m      DateOfSalePage.instance.required.month.underlying.sendKeys(alphaChars.concat(today.toString("MM")))[m
[32m+[m[32m      DateOfSalePage.instance.required.year.underlying.sendKeys(alphaChars.concat(today.toString("YYYY")))[m
[32m+[m
[32m+[m[32m      DateOfSalePage.instance.required.day.value should equal(today.toString("dd"))[m
[32m+[m[32m      DateOfSalePage.instance.required.month.value should equal(today.toString("MM"))[m
[32m+[m[32m      DateOfSalePage.instance.required.year.value should equal(today.toString("YYYY"))[m
[32m+[m[32m    }[m
   }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/FeedbackFormIntegrationSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/FeedbackFormIntegrationSpec.scala[m
[1mindex b745d53..da05a71 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/FeedbackFormIntegrationSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/FeedbackFormIntegrationSpec.scala[m
[36m@@ -33,5 +33,17 @@[m [mclass FeedbackFormIntegrationSpec extends UiSpec with TestHarness {[m
       FeedbackFormPage.navigate(feedback = "1" * 1201)[m
       ErrorPanel.numberOfErrors should equal(1)[m
     }[m
[32m+[m
[32m+[m[32m    "show a count down of characters remaining as user types in text" in new WebBrowserWithJs {[m
[32m+[m[32m      go to FeedbackFormPage[m
[32m+[m
[32m+[m[32m      FeedbackFormPage.waitUntilJavascriptReady[m
[32m+[m
[32m+[m[32m      FeedbackFormPage.feedbackElement.value = ""[m
[32m+[m[32m      FeedbackFormPage.feedbackCounterElement.text should equal("500")[m
[32m+[m
[32m+[m[32m      FeedbackFormPage.feedbackElement.underlying.sendKeys("1234567890")[m
[32m+[m[32m      FeedbackFormPage.feedbackCounterElement.text should equal("490")[m
[32m+[m[32m    }[m
   }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/OptionToggleIntegrationSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/OptionToggleIntegrationSpec.scala[m
[1mindex f714426..d0e77f1 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/OptionToggleIntegrationSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/OptionToggleIntegrationSpec.scala[m
[36m@@ -1,69 +1,78 @@[m
 package uk.gov.dvla.vehicles.presentation.common.views[m
 [m
[31m-import org.openqa.selenium.SearchContext[m
[31m-import org.scalatest.selenium.WebBrowser.click[m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[31m-import org.scalatest.selenium.WebBrowser.pageTitle[m
[31m-import org.scalatest.selenium.WebBrowser.pageSource[m
[32m+[m[32mimport org.openqa.selenium.WebDriver[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser.{click, go,pageSource, pageTitle}[m
 import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec[m
[31m-import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebDriverFactory[m
[31m-import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle[m
 import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, OptionTogglePage}[m
[32m+[m[32mimport uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle[m
 [m
 class OptionToggleIntegrationSpec extends UiSpec with TestHarness {[m
 [m
[31m-  "present" ignore {[m
[31m-    "Display all the three testing components with the correct labels" in new WebBrowserForSelenium {[m
[32m+[m[32m  "present" should {[m
[32m+[m[32m    "Display all the three testing components with the correct labels" in new WebBrowserWithJs {[m
       go to OptionTogglePage[m
       pageTitle should equal(OptionTogglePage.title)[m
 [m
[31m-      val textRadio = OptionTogglePage.textRadio[m
[32m+[m[32m      val textOptionGroup = OptionTogglePage.textRadioOption[m
[32m+[m[32m      textOptionGroup.label.getText should equal("Do you want to enter a string?")[m
[32m+[m[32m      textOptionGroup.radio.selection should equal(None)[m
[32m+[m[32m      textOptionGroup.component.underlying.getText should equal("")[m
 [m
[31m-      textRadio.radio.selection should equal(None)[m
[31m-      textRadio.label.text should equal("Do you want to enter a string?")[m
[31m-      textRadio.component.text should equal("")[m
[32m+[m[32m      val intRadioOption = OptionTogglePage.intRadioOption[m
 [m
[31m-      val intRadio = OptionTogglePage.intRadio[m
[31m-      intRadio.radio.selection should equal(None)[m
[31m-      intRadio.label.text should equal("Do you want to enter an Int?")[m
[31m-      intRadio.component.text should equal("")[m
[32m+[m[32m      intRadioOption.radio.selection should equal(None)[m
[32m+[m[32m      intRadioOption.label.getText should equal("Do you want to enter an Int?")[m
[32m+[m[32m      intRadioOption.component.text should equal("")[m
 [m
[31m-      val dateRadio = OptionTogglePage.dateRadio[m
[31m-      dateRadio.radio.selection should equal(None)[m
[31m-      dateRadio.label.text should equal("Do you want to enter a Date?")[m
[31m-      val dateComponent = dateRadio.component[m
[32m+[m[32m      val dateOptionGroup = OptionTogglePage.dateRadioOption[m
[32m+[m[32m      dateOptionGroup.radio.selection should equal(None)[m
[32m+[m[32m      dateOptionGroup.label.getText should equal("Do you want to enter a Date?")[m
[32m+[m[32m      val dateComponent = dateOptionGroup.component[m
       dateComponent.day.text should equal("")[m
       dateComponent.month.text should equal("")[m
       dateComponent.year.text should equal("")[m
     }[m
 [m
[31m-    "show working option toggle" in new WebBrowserForSelenium(webDriver = WebDriverFactory.defaultBrowserPhantomJs) {[m
[32m+[m[32m    "show working option toggle" in new WebBrowserWithJs {[m
       go to OptionTogglePage[m
       pageTitle should equal(OptionTogglePage.title)[m
 [m
[31m-      OptionTogglePage.textRadio.assetComponentInvisible[m
[31m-      OptionTogglePage.textRadio.radio.value = "visible"[m
[31m-      OptionTogglePage.textRadio.assetComponentVisible[m
[31m-      OptionTogglePage.textRadio.radio.value = "invisible"[m
[31m-      OptionTogglePage.textRadio.assetComponentInvisible[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentInvisible[m
[32m+[m[32m      OptionTogglePage.textRadioOption.radio.value = "visible"[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentVisible[m
[32m+[m[32m      OptionTogglePage.textRadioOption.radio.value = "invisible"[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentInvisible[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "show/hide the options when yes/no is clicked" in new WebBrowserWithJs {[m
[32m+[m[32m      go to OptionTogglePage[m
[32m+[m
[32m+[m[32m      OptionTogglePage.waitUntilJavascriptReady[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentInvisible[m
[32m+[m
[32m+[m[32m      click on OptionTogglePage.textRadioOption.radioInputWithValue(OptionalToggle.Visible)[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentVisible[m
[32m+[m
[32m+[m[32m      click on OptionTogglePage.textRadioOption.radioInputWithValue(OptionalToggle.Invisible)[m
[32m+[m[32m      OptionTogglePage.textRadioOption.assetComponentInvisible[m
     }[m
   }[m
 [m
[31m-  "submit" ignore {[m
[31m-    "should remember the radio state" in new WebBrowserForSelenium {[m
[32m+[m[32m  "submit" should {[m
[32m+[m[32m    "should remember the radio state" in new WebBrowserWithJs {[m
       go to OptionTogglePage[m
 [m
[31m-      OptionTogglePage.textRadio.radio.value = OptionalToggle.Visible[m
[31m-      OptionTogglePage.dateRadio.radio.value = OptionalToggle.Visible[m
[32m+[m[32m      OptionTogglePage.textRadioOption.radio.value = OptionalToggle.Visible[m
[32m+[m[32m      OptionTogglePage.dateRadioOption.radio.value = OptionalToggle.Visible[m
       click on OptionTogglePage.submit[m
 [m
[31m-      OptionTogglePage.textRadio.radio.selection should equal(Some(OptionalToggle.Visible))[m
[31m-      OptionTogglePage.intRadio.radio.selection should equal(None)[m
[31m-      OptionTogglePage.dateRadio.radio.selection should equal(Some(OptionalToggle.Visible))[m
[32m+[m[32m      OptionTogglePage.textRadioOption.radio.selection should equal(Some(OptionalToggle.Visible))[m
[32m+[m[32m      OptionTogglePage.intRadioOption.radio.selection should equal(None)[m
[32m+[m[32m      OptionTogglePage.dateRadioOption.radio.selection should equal(Some(OptionalToggle.Visible))[m
     }[m
 [m
[31m-    "should require options to be selected" in new WebBrowserForSelenium {[m
[32m+[m[32m    "should require options to be selected" in new WebBrowserWithJs {[m
       go to OptionTogglePage[m
 [m
       click on OptionTogglePage.submit[m
[36m@@ -73,9 +82,10 @@[m [mclass OptionToggleIntegrationSpec extends UiSpec with TestHarness {[m
         "optional-date-option - mandatory-alternative.not-selected"[m
       )[m
 [m
[31m-      val textRadio = OptionTogglePage.textRadio[m
[31m-      textRadio.radio.value = OptionalToggle.Visible[m
[31m-      textRadio.component.value = "some text"[m
[32m+[m[32m      val textOptionGroup = OptionTogglePage.textRadioOption[m
[32m+[m[32m      textOptionGroup.radio.value = OptionalToggle.Visible[m
[32m+[m[32m      textOptionGroup.assetComponentVisible[m
[32m+[m[32m      textOptionGroup.component.value = "some text"[m
       click on OptionTogglePage.submit[m
 [m
       verifyErrors([m
[36m@@ -84,23 +94,23 @@[m [mclass OptionToggleIntegrationSpec extends UiSpec with TestHarness {[m
       )[m
     }[m
 [m
[31m-    "should run the underlying validations" in new WebBrowserForSelenium {[m
[32m+[m[32m    "should run the underlying validations" in new WebBrowserWithJs {[m
       go to OptionTogglePage[m
 [m
[31m-      OptionTogglePage.textRadio.radio.value = OptionalToggle.Invisible[m
[31m-      OptionTogglePage.intRadio.radio.value = OptionalToggle.Invisible[m
[31m-      OptionTogglePage.dateRadio.radio.value = OptionalToggle.Visible[m
[32m+[m[32m      OptionTogglePage.textRadioOption.radio.value = OptionalToggle.Invisible[m
[32m+[m[32m      OptionTogglePage.intRadioOption.radio.value = OptionalToggle.Invisible[m
[32m+[m[32m      OptionTogglePage.dateRadioOption.radio.value = OptionalToggle.Visible[m
       click on OptionTogglePage.submit[m
       verifyErrors([m
         "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"[m
       )[m
[31m-[m
[31m-      OptionTogglePage.dateRadio.component.day.value = "12"[m
[32m+[m[32m      OptionTogglePage.dateRadioOption.assetComponentVisible[m
[32m+[m[32m      OptionTogglePage.dateRadioOption.component.day.value = "12"[m
       click on OptionTogglePage.submit[m
       verifyErrors([m
         "optional-date - Please enter a valid date in the format DD MM YYYY for example 20 3 1976 or 1 03 1976"[m
       )[m
[31m-      val dateComponent = OptionTogglePage.dateRadio.component[m
[32m+[m[32m      val dateComponent = OptionTogglePage.dateRadioOption.component[m
       dateComponent.day.value should equal("12")[m
       dateComponent.month.value should equal("")[m
       dateComponent.year.value should equal("")[m
[36m@@ -111,22 +121,12 @@[m [mclass OptionToggleIntegrationSpec extends UiSpec with TestHarness {[m
       click on OptionTogglePage.submit[m
       pageTitle should equal("Success")[m
       pageSource should include("Some(2012-12-12)")[m
[31m-[m
     }[m
   }[m
 [m
[31m-  "javascript prototype" ignore {[m
[31m-    "qunit tests should pass" in new WebBrowserForSelenium(webDriver = WebDriverFactory.defaultBrowserPhantomJs) {[m
[31m-      go to OptionTogglePage.jsTestUrl[m
[31m-      assertJsTestPass[m
[31m-    }[m
[31m-  }[m
[31m-[m
[31m-[m
[31m-[m
[31m-  private def verifyErrors(errors: String*)(implicit driver: SearchContext): Unit = {[m
[32m+[m[32m  private def verifyErrors(errors: String*)(implicit driver: WebDriver): Unit = {[m
     val errorsStr = ErrorPanel.text[m
     errors.foreach(errorsStr should include(_))[m
     ErrorPanel.numberOfErrors should equal(errors.length)[m
   }[m
[31m-}[m
[32m+[m[32m}[m
\ No newline at end of file[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/SummaryWrapperSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/SummaryWrapperSpec.scala[m
[1mindex 41a16f4..b7c2de4 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/SummaryWrapperSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/SummaryWrapperSpec.scala[m
[36m@@ -1,7 +1,7 @@[m
 package uk.gov.dvla.vehicles.presentation.common.views[m
 [m
 import org.scalatest.AppendedClues[m
[31m-import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser.{click, go, pageTitle}[m
 import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec[m
 import uk.gov.dvla.vehicles.presentation.common.pages.SummaryWrapperPage[m
[36m@@ -13,28 +13,33 @@[m [mclass SummaryWrapperSpec extends UiSpec with TestHarness with AppendedClues {[m
       go to SummaryWrapperPage[m
       pageTitle should equal(SummaryWrapperPage.title)[m
 [m
[31m-      !SummaryWrapperPage.contentElement.isDisplayed[m
[32m+[m[32m      SummaryWrapperPage.assetComponentInvisible(SummaryWrapperPage.contentCssSelector)[m
     }[m
[31m-    "have the content shown on page load without Javascript enabled" in new WebBrowserWithJsDisabled {[m
[32m+[m
[32m+[m[32m    "content should be visible with Javascript disabled" in new WebBrowserWithJs {[m
       go to SummaryWrapperPage[m
 [m
[31m-      SummaryWrapperPage.contentElement.isDisplayed[m
[32m+[m[32m      SummaryWrapperPage.showHideTriggerElement.underlying.getAttribute("class").contains("no-js-only")[m
     }[m
[32m+[m
     "have an element which can show hide content " in new WebBrowserWithJs {[m
       go to SummaryWrapperPage[m
 [m
[31m-      SummaryWrapperPage.showHideTriggerElement.isDisplayed[m
[32m+[m[32m      SummaryWrapperPage.assetComponentVisible(SummaryWrapperPage.showHideTriggerCssSelector)[m
     }[m
[32m+[m
     "show content when show/hide is clicked, then hide it again on a second click" in new WebBrowserWithJs  {[m
       go to SummaryWrapperPage[m
 [m
[32m+[m[32m      SummaryWrapperPage.waitUntilJavascriptReady[m
[32m+[m
       // First click to show the content[m
[31m-      click on SummaryWrapperPage.showHideTriggerElement[m
[31m-      SummaryWrapperPage.contentElement.isDisplayed[m
[32m+[m[32m      click on SummaryWrapperPage.showHideTriggerElement.underlying[m
[32m+[m[32m      SummaryWrapperPage.assetComponentVisible(SummaryWrapperPage.contentCssSelector)[m
 [m
       // Second click, should hide the content[m
[31m-      click on SummaryWrapperPage.showHideTriggerElement[m
[31m-      !SummaryWrapperPage.contentElement.isDisplayed[m
[32m+[m[32m      click on SummaryWrapperPage.showHideTriggerElement.underlying[m
[32m+[m[32m      SummaryWrapperPage.assetComponentInvisible(SummaryWrapperPage.contentCssSelector)[m
     }[m
   }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/V5cRegistrationNumberIntegrationSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/V5cRegistrationNumberIntegrationSpec.scala[m
[1mindex 6807be1..280352b 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/V5cRegistrationNumberIntegrationSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/V5cRegistrationNumberIntegrationSpec.scala[m
[36m@@ -1,10 +1,10 @@[m
 package uk.gov.dvla.vehicles.presentation.common.views[m
 [m
[31m-import org.scalatest.selenium.WebBrowser.go[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser.{click, go}[m
 import org.scalatest.selenium.WebBrowser.pageTitle[m
 import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec[m
[31m-import uk.gov.dvla.vehicles.presentation.common.pages.{ErrorPanel, V5cRegistrationNumberPage}[m
[32m+[m[32mimport uk.gov.dvla.vehicles.presentation.common.pages.{V5cRegistrationNumberPage, ErrorPanel}[m
 [m
 class V5cRegistrationNumberIntegrationSpec extends UiSpec with TestHarness {[m
 [m
[36m@@ -23,5 +23,40 @@[m [mclass V5cRegistrationNumberIntegrationSpec extends UiSpec with TestHarness {[m
       V5cRegistrationNumberPage.navigate(v5cRegistrationNumber = "")[m
       ErrorPanel.numberOfErrors should equal(3)[m
     }[m
[32m+[m
[32m+[m[32m    "have a visible help icon with Javascript enabled" in new WebBrowserWithJs {[m
[32m+[m[32m      go to V5cRegistrationNumberPage[m
[32m+[m
[32m+[m[32m      V5cRegistrationNumberPage.assetComponentVisible(".field-help")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "have a hidden help content with Javascript enabled" in new WebBrowserWithJs {[m
[32m+[m[32m      go to V5cRegistrationNumberPage[m
[32m+[m[32m      V5cRegistrationNumberPage.assetComponentInvisible(".field-help-content")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "show help content when the help icon is clicked" in new WebBrowserWithJs {[m
[32m+[m[32m      go to V5cRegistrationNumberPage[m
[32m+[m[32m      V5cRegistrationNumberPage.waitUntilJavascriptReady[m
[32m+[m
[32m+[m[32m      click on V5cRegistrationNumberPage.helpIcon[m
[32m+[m[32m      V5cRegistrationNumberPage.assetComponentVisible(".field-help-content")[m
[32m+[m
[32m+[m[32m      click on V5cRegistrationNumberPage.helpIcon[m
[32m+[m[32m      V5cRegistrationNumberPage.assetComponentInvisible(".field-help-content")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m
[32m+[m[32m    "have hidden help icon with Javascript disabled" in new WebBrowserWithJsDisabled {[m
[32m+[m[32m      go to V5cRegistrationNumberPage[m
[32m+[m
[32m+[m[32m      V5cRegistrationNumberPage.helpIcon.attribute("class").toString.contains("js-only") should equal(true)[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "have visible help content with Javascript disabled" in new WebBrowserWithJsDisabled {[m
[32m+[m[32m      go to V5cRegistrationNumberPage[m
[32m+[m
[32m+[m[32m      V5cRegistrationNumberPage.helpContent.attribute("class").toString.contains("no-js-only") should equal(true)[m
[32m+[m[32m    }[m
   }[m
 }[m
[1mdiff --git a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/ValtechDeclareCheckIntegrationSpec.scala b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/ValtechDeclareCheckIntegrationSpec.scala[m
[1mindex d84c9dc..b357459 100644[m
[1m--- a/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/ValtechDeclareCheckIntegrationSpec.scala[m
[1m+++ b/common-test/test/uk/gov/dvla/vehicles/presentation/common/views/ValtechDeclareCheckIntegrationSpec.scala[m
[36m@@ -1,6 +1,6 @@[m
 package uk.gov.dvla.vehicles.presentation.common.views[m
 [m
[31m-import org.scalatest.selenium.WebBrowser.pageTitle[m
[32m+[m[32mimport org.scalatest.selenium.WebBrowser.{pageTitle, click}[m
 import org.scalatest.selenium.WebBrowser.go[m
 import uk.gov.dvla.vehicles.presentation.common.composition.TestHarness[m
 import uk.gov.dvla.vehicles.presentation.common.helpers.UiSpec[m
[36m@@ -23,5 +23,40 @@[m [mclass ValtechDeclareCheckIntegrationSpec extends UiSpec with TestHarness {[m
       ValtechDeclareCheckPage.sadPath()[m
       ErrorPanel.numberOfErrors should equal(1)[m
     }[m
[32m+[m
[32m+[m[32m    "have a visible help icon with Javascript enabled" in new WebBrowserWithJs() {[m
[32m+[m[32m      go to ValtechDeclareCheckPage[m
[32m+[m
[32m+[m[32m      ValtechDeclareCheckPage.assetComponentVisible(".field-help")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "have a hidden help content with Javascript enabled" in new WebBrowserWithJs {[m
[32m+[m[32m      go to ValtechDeclareCheckPage[m
[32m+[m[32m      ValtechDeclareCheckPage.assetComponentInvisible(".field-help-content")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "show help content when the help icon is clicked" in new WebBrowserWithJs {[m
[32m+[m[32m      go to ValtechDeclareCheckPage[m
[32m+[m[32m      ValtechDeclareCheckPage.waitUntilJavascriptReady[m
[32m+[m
[32m+[m[32m      click on ValtechDeclareCheckPage.helpIcon[m
[32m+[m[32m      ValtechDeclareCheckPage.assetComponentVisible(".field-help-content")[m
[32m+[m
[32m+[m[32m      click on ValtechDeclareCheckPage.helpIcon[m
[32m+[m[32m      ValtechDeclareCheckPage.assetComponentInvisible(".field-help-content")[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "have hidden help icon with Javascript disabled" in new WebBrowserWithJsDisabled {[m
[32m+[m[32m      go to ValtechDeclareCheckPage[m
[32m+[m
[32m+[m[32m      ValtechDeclareCheckPage.helpIcon.attribute("class").toString.contains("js-only") should equal(true)[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    "have visible help content with Javascript disabled" in new WebBrowserWithJsDisabled {[m
[32m+[m[32m      go to ValtechDeclareCheckPage[m
[32m+[m
[32m+[m[32m      ValtechDeclareCheckPage.helpContent.attribute("class").toString.contains("no-js-only") should equal(true)[m
[32m+[m[32m    }[m
[32m+[m
   }[m
 }[m
[1mdiff --git a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/Page.scala b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/Page.scala[m
[1mindex 6cedffc..3d96225 100644[m
[1m--- a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/Page.scala[m
[1m+++ b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/Page.scala[m
[36m@@ -20,6 +20,26 @@[m
 [m
 package uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser[m
 [m
[32m+[m[32mimport org.openqa.selenium.{By, WebDriver}[m
[32m+[m[32mimport org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}[m
[32m+[m[32mimport uk.gov.dvla.vehicles.presentation.common.views.widgetdriver.Wait[m
[32m+[m
 trait Page extends org.scalatest.selenium.Page {[m
   val title: String[m
[32m+[m
[32m+[m[32m  def waitUntilJavascriptReady(implicit driver: WebDriver): Unit = {[m
[32m+[m[32m    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".js-ready")))[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  def assetComponentInvisible(cssClass: String)(implicit driver: WebDriver) =[m
[32m+[m[32m    new WebDriverWait(driver, 7)[m
[32m+[m[32m      .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(cssClass)))[m
[32m+[m
[32m+[m[32m  def assetComponentVisible(cssClass: String)(implicit driver: WebDriver) =[m
[32m+[m[32m    new WebDriverWait(driver, 7)[m
[32m+[m[32m      .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(cssClass)))[m
[32m+[m
[32m+[m[32m  def elementHasClass(cssSelector: String, hasClass: String)(implicit driver: WebDriver): Boolean =[m
[32m+[m[32m    driver.findElement(By.cssSelector(cssSelector)).getAttribute("class").contains(hasClass)[m
[32m+[m
 }[m
[1mdiff --git a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/TestHarnessBase.scala b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/TestHarnessBase.scala[m
[1mindex 22d7187..02efab7 100644[m
[1m--- a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/TestHarnessBase.scala[m
[1m+++ b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/TestHarnessBase.scala[m
[36m@@ -46,6 +46,12 @@[m [mtrait TestHarnessBase extends ProgressBar with GlobalCreator {[m
     webDriver = WebDriverFactory.defaultBrowserPhantomJs[m
   )[m
 [m
[32m+[m[32m  abstract class PhantomJsWithJsDisabled extends WebBrowserForSelenium([m
[32m+[m[32m    webDriver = WebDriverFactory.defaultBrowserPhantomJsNoJs[m
[32m+[m
[32m+[m
[32m+[m[32m  )[m
[32m+[m
   object WebBrowser {[m
 [m
     private[TestHarnessBase] lazy val fakeAppWithTestGlobal: FakeApplication = LightFakeApplication(global)[m
[1mdiff --git a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/WebDriverFactory.scala b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/WebDriverFactory.scala[m
[1mindex eaa1758..c34f464 100644[m
[1m--- a/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/WebDriverFactory.scala[m
[1m+++ b/test/uk/gov/dvla/vehicles/presentation/common/helpers/webbrowser/WebDriverFactory.scala[m
[36m@@ -85,6 +85,7 @@[m [mobject WebDriverFactory {[m
   private def htmlUnitDriver(javascriptEnabled: Boolean) = {[m
     val driver = new HtmlUnitDriver()[m
     driver.setJavascriptEnabled(javascriptEnabled) // TODO HTMLUnit blows up when navigating live site due to JavaScript errors![m
[32m+[m
     driver[m
   }[m
 [m
[1mdiff --git a/test/uk/gov/dvla/vehicles/presentation/common/views/widgetdriver/AddressPickerDriver.scala b/test/uk/gov/dvla/vehicles/presentation/common/views/widgetdriver/AddressPickerDriver.scala[m
[1mindex c09d014..dbcfcda 100644[m
[1m--- a/test/uk/gov/dvla/vehicles/presentation/common/views/widgetdriver/AddressPickerDriver.scala[m
[1m+++ b/test/uk/gov/dvla/vehicles/presentation/common/views/widgetdriver/AddressPickerDriver.scala[m
[36m@@ -11,7 +11,7 @@[m [mimport uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.PostcodeI[m
 import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.PostTownId[m
 import uk.gov.dvla.vehicles.presentation.common.mappings.AddressPicker.RememberId[m
 [m
[31m-class AddressPickerDriver(id: String) extends WebBrowser {[m
[32m+[m[32mclass AddressPickerDriver(id: String) extends WebBrowser  {[m
   private implicit val timeout = 3[m
 [m
   def postCodeSearch(implicit driver: WebDriver): TextField =[m
[36m@@ -25,6 +25,7 @@[m [mclass AddressPickerDriver(id: String) extends WebBrowser {[m
     find(cssSelector(".address-manual-toggle"))//(driver.findElement(By.id(id)))[m
       .getOrElse(throw new Exception(s"Cannot find element with id address-manual-toggle in address picker with id:$id "))[m
 [m
[32m+[m
   def search(postcode: String)(implicit driver: WebDriver): Unit = {[m
     postCodeSearch.value = postcode[m
     click on searchButton[m
[36m@@ -37,7 +38,7 @@[m [mclass AddressPickerDriver(id: String) extends WebBrowser {[m
   }[m
 [m
   def addressSelect(implicit driver: WebDriver): SingleSel =[m
[31m-    singleSel(cssSelector(".js-address-list"))//(driver.findElement(By.id(id)))[m
[32m+[m[32m    singleSel(cssSelector(".js-address-list"))   //(driver.findElement(By.id(id)))[m
 [m
   def selectAddress(value: String)(implicit driver: WebDriver): Unit = {[m
     addressSelect.value = value[m
[36m@@ -61,6 +62,10 @@[m [mclass AddressPickerDriver(id: String) extends WebBrowser {[m
   def remember(implicit driver: WebDriver): Checkbox =[m
     checkbox(id(s"${id}_$RememberId"))//(driver.findElement(By.id(id)))[m
 [m
[32m+[m[32m  def assertAddressVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {[m
[32m+[m[32m    Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id")))[m
[32m+[m[32m  }[m
[32m+[m
   def assertAddressInputsVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {[m
     Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .address-manual-inputs-wrapper")))[m
   }[m
[36m@@ -81,6 +86,10 @@[m [mclass AddressPickerDriver(id: String) extends WebBrowser {[m
     Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))[m
   }[m
 [m
[32m+[m[32m  def assertLookupInputInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {[m
[32m+[m[32m    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))[m
[32m+[m[32m  }[m
[32m+[m
   def assertServerErrorVisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {[m
     Wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(s"#$id .server-message")))[m
   }[m
[36m@@ -97,10 +106,6 @@[m [mclass AddressPickerDriver(id: String) extends WebBrowser {[m
     Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .missing-postcode")))[m
   }[m
 [m
[31m-  def assertLookupInputInvisible(timeout: Int = timeout)(implicit driver: WebDriver): Unit = {[m
[31m-    Wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(s"#$id .postcode-lookup-container")))[m
[31m-  }[m
[31m-[m
   def enterAddressManuallyLink(implicit driver: WebDriver): Element =[m
     find(cssSelector(s"#$id .address-manual-toggle")) getOrElse([m
       throw new Exception(s"Unable to find element with id css selector (#$id .address-manual-toggle)")[m
