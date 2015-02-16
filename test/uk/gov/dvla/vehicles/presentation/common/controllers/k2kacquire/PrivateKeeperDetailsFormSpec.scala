package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import org.joda.time.LocalDate
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.model.{CacheKeyPrefix, PrivateKeeperDetailsFormModel}
import common.model.PrivateKeeperDetailsFormModel.Form.TitleId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailId
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameId
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameAndTitleMaxLength
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameMinLength
import common.model.PrivateKeeperDetailsFormModel.Form.DriverNumberId
import common.model.PrivateKeeperDetailsFormModel.Form.PostcodeId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameMaxLength
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameMinLength
import common.model.PrivateKeeperDetailsFormModel.Form.DateOfBirthId
import common.mappings.DayMonthYear.{YearId, MonthId, DayId}
import common.mappings.{TitleType, TitlePickerString}
import common.{UnitSpec, WithApplication}
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.services.DateServiceImpl

class PrivateKeeperDetailsFormSpec extends UnitSpec {

  final val DayDateOfBirthValid = "24"
  final val DriverNumberValid = "ABCD9711215EFLGH"
  final val EmailValid = "my@email.com"
  final val FirstNameValid = "fn"
  final val LastNameValid = "TestLastName"
  final val MonthDateOfBirthValid = "12"
  final val PostcodeValid = "QQ99QQ"
  final val YearDateOfBirthValid = "1920"

  "form" should {
    "accept if form is completed with all fields correctly" in new WithApplication {
      val model = formWithValidDefaults().get
//      model.title should equal(TitleType(1, ""))
      model.firstName should equal(FirstNameValid)
      model.lastName should equal(LastNameValid)
//      model.dateOfBirth should equal(Some(new LocalDate( TODO put me back
//        YearDateOfBirthValid.toInt,
//        MonthDateOfBirthValid.toInt,
//        DayDateOfBirthValid.toInt)))
      model.email should equal(Some(EmailValid))
      model.driverNumber should equal(Some(DriverNumberValid))
      model.postcode should equal(PostcodeValid)
    }

    "accept if form is completed with mandatory fields only" in new WithApplication {
      val model = formWithValidDefaults(
        dayDateOfBirth = "",
        monthDateOfBirth = "",
        yearDateOfBirth = "",
        email = "",
        driverNumber = "").get
//      model.title should equal(TitleType(1, ""))
      model.firstName should equal(FirstNameValid)
      model.lastName should equal(LastNameValid)
//      model.dateOfBirth should equal(None) TODO put me back
      model.email should equal(None)
      model.driverNumber should equal(None)
      model.postcode should equal(PostcodeValid)
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(title = "", firstName = "", lastName = "", email = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.title.unknownOption", "error.validFirstName", "error.minLength", "error.required", "error.validLastName")
    }
  }

//  "title" should {
//    "reject if no selection is made" in new WithApplication {
//      formWithValidDefaults(title = "").errors.flatMap(_.messages) should contain theSameElementsAs
//        List("error.title.unknownOption")
//    }
//
//    "accept if title is selected" in new WithApplication {
//      val model = formWithValidDefaults(title = "2").get
//      model.title should equal(TitleType(2, ""))
//    }
//  }

  "email" should {
    "accept in valid format" in new WithApplication {
      val model = formWithValidDefaults(email = EmailValid).get
      model.email should equal(Some(EmailValid))
    }

    "accept with no entry" in new WithApplication {
      val model = formWithValidDefaults(email = "").get
      model.email should equal(None)
    }

    "reject if incorrect format" in new WithApplication {
      formWithValidDefaults(email = "no_at_symbol.com").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if less than min length" in new WithApplication {
      formWithValidDefaults(email = "no").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if greater than max length" in new WithApplication {
      formWithValidDefaults(email = "n@" + ("a" * 248) + ".com").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }
  }

  "firstName" should {
    "reject if empty" in new WithApplication {
      formWithValidDefaults(firstName = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validFirstName")
    }

    "reject if greater than max length" in new WithApplication {
      formWithValidDefaults(firstName = "a" * (FirstNameAndTitleMaxLength + 1)).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.titlePlusFirstName.tooLong")
    }

    "reject if denied special characters are present $" in new WithApplication {
      formWithValidDefaults(firstName = FirstNameValid + "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validFirstName")
    }

    "reject if denied special characters are present +" in new WithApplication {
      formWithValidDefaults(firstName = FirstNameValid + "+").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validFirstName")
    }

    "reject if denied special characters are present ^" in new WithApplication {
      formWithValidDefaults(firstName = FirstNameValid + "^").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validFirstName")
    }

    "reject if denied special characters are present *" in new WithApplication {
      formWithValidDefaults(firstName = FirstNameValid + "*").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validFirstName")
    }

//    "accept if equal to max length" in new WithApplication {
//      val maxFirstName = "a" * (FirstNameAndTitleMaxLength - 1)
//      val formModel = validFormModel(title = "4", otherTitle = "x", firstName = maxFirstName)
//      formModel.firstName should equal(maxFirstName)
//      formModel.title should equal(TitleType(4, "x"))
//    }

//    "don't accept if title plus first name is above the max" in new WithApplication {
//      val title = "sometitle"
//      val tooLongFirstName = "a" * (FirstNameAndTitleMaxLength - title.length + 1)
//      formWithValidDefaults(title = "4", otherTitle = title, firstName = tooLongFirstName)
//        .errors.flatMap(_.messages) should contain theSameElementsAs List("error.titlePlusFirstName.tooLong")
//
//      val longEnoughFirstName = "a" * (FirstNameAndTitleMaxLength - title.length)
//      val formModel = validFormModel(title = "4", otherTitle = title, firstName = longEnoughFirstName)
//      formModel.firstName should equal(longEnoughFirstName)
//      formModel.title should equal(TitleType(4, title))
//    }

    "accept if equal to min length" in new WithApplication {
      val model = formWithValidDefaults(firstName = "a" * FirstNameMinLength).get
      model.firstName should equal("a" * FirstNameMinLength)
    }

    "accept in valid format" in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid).get
      model.firstName should equal(FirstNameValid)
    }

    "accept allowed special characters ." in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid + ".").get
      model.firstName should equal(FirstNameValid + ".")
    }

    "accept allowed special characters ," in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid + ",").get
      model.firstName should equal( FirstNameValid + ",")
    }

    "accept allowed special characters -" in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid + "'").get
      model.firstName should equal(FirstNameValid + "'")
    }

    "accept allowed special characters \"" in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid + "'").get
      model.firstName should equal(FirstNameValid + "'")
    }

    "accept allowed special characters '" in new WithApplication {
      val model = formWithValidDefaults(firstName = FirstNameValid + "'").get
      model.firstName should equal(FirstNameValid + "'")
    }

    "accept when a space is present within the first name" in new WithApplication {
      val model = formWithValidDefaults(firstName = "a" + " " + "a").get
      model.firstName should equal("a" + " " + "a")
    }
  }

  "lastName" should {
    "reject if empty" in new WithApplication {
      formWithValidDefaults(lastName = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.validLastName", "error.required")
    }

    "reject if greater than max length" in new WithApplication {
      formWithValidDefaults(lastName = "a" * (LastNameMaxLength + 1)).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if denied special characters are present $" in new WithApplication {
      formWithValidDefaults(lastName = LastNameValid + "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validLastName")
    }

    "reject if denied special characters are present +" in new WithApplication {
      formWithValidDefaults(lastName = LastNameValid + "+").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validLastName")
    }

    "reject if denied special characters are present ^" in new WithApplication {
      formWithValidDefaults(lastName = LastNameValid + "^").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validLastName")
    }

    "reject if denied special characters are present *" in new WithApplication {
      formWithValidDefaults(lastName = LastNameValid + "*").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.validLastName")
    }

    "accept if equal to max length" in new WithApplication {
      val model = formWithValidDefaults(lastName = "a" * LastNameMaxLength).get
      model.lastName should equal("a" * LastNameMaxLength)
    }

    "accept if equal to min length" in new WithApplication {
      val model = formWithValidDefaults(lastName = "a" * LastNameMinLength).get
      model.lastName should equal("a" * LastNameMinLength)
    }

    "accept in valid format" in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid).get
      model.lastName should equal(LastNameValid)
    }

    "accept allowed special characters ." in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid + ".").get
      model.lastName should equal(LastNameValid + ".")
    }

    "accept allowed special characters ," in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid + ",").get
      model.lastName should equal( LastNameValid + ",")
    }

    "accept allowed special characters -" in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid + "'").get
      model.lastName should equal(LastNameValid + "'")
    }

    "accept allowed special characters \"" in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid + "'").get
      model.lastName should equal(LastNameValid + "'")
    }

    "accept allowed special characters '" in new WithApplication {
      val model = formWithValidDefaults(lastName = LastNameValid + "'").get
      model.lastName should equal(LastNameValid + "'")
    }

    "accept when a space is present within the first name" in new WithApplication {
      val model = formWithValidDefaults(lastName = "a" + " " + "a").get
      model.lastName should equal("a" + " " + "a")
    }
  }

  "date of birth" should {
    "not accept an invalid day of month of 0" in new WithApplication {
      formWithValidDefaults(dayDateOfBirth = "0").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept an invalid day of month of 32" in new WithApplication {
      formWithValidDefaults(dayDateOfBirth = "32").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept an invalid month of 0" in new WithApplication {
      formWithValidDefaults(monthDateOfBirth = "0").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept an invalid month of 13" in new WithApplication {
      formWithValidDefaults(monthDateOfBirth = "13").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept special characters in day field" in new WithApplication {
      formWithValidDefaults(dayDateOfBirth = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept special characters in month field" in new WithApplication {
      formWithValidDefaults(monthDateOfBirth = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept special characters in year field" in new WithApplication {
      formWithValidDefaults(yearDateOfBirth = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept letters in day field" in new WithApplication {
      formWithValidDefaults(dayDateOfBirth = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept letters in month field" in new WithApplication {
      formWithValidDefaults(monthDateOfBirth = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

    "not accept lettersin year field" in new WithApplication {
      formWithValidDefaults(yearDateOfBirth = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.dateOfBirth.invalid")
    }

//    "accept if date of birth is entered correctly" in new WithApplication { //TODO put me back
//      val model = formWithValidDefaults(
//        dayDateOfBirth = DayDateOfBirthValid,
//        monthDateOfBirth = MonthDateOfBirthValid,
//        yearDateOfBirth = YearDateOfBirthValid).get
//
//      model.dateOfBirth should equal(Some(new LocalDate(
//        YearDateOfBirthValid.toInt,
//        MonthDateOfBirthValid.toInt,
//        DayDateOfBirthValid.toInt)))
//    }
  }

  "postcode" should {
    "reject if postcode is empty" in new WithApplication {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is less than the minimum length" in new WithApplication {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is more than the maximum length" in new WithApplication {
      formWithValidDefaults(postcode = "SA99 1DDD").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength", "error.restricted.validPostcode")
    }

    "reject if postcode contains special characters" in new WithApplication {
      formWithValidDefaults(postcode = "SA99 1D$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "reject if postcode contains an incorrect format" in new WithApplication {
      formWithValidDefaults(postcode = "SAR99").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "accept when a valid postcode is entered" in new WithApplication {
      val model = formWithValidDefaults(postcode = PostcodeValid).get
      model.postcode should equal(PostcodeValid)
    }
  }

  private def validFormModel(title: String = "1",
                                 otherTitle: String = "",
                                 firstName: String = FirstNameValid,
                                 lastName: String = LastNameValid,
                                 dayDateOfBirth: String = DayDateOfBirthValid,
                                 monthDateOfBirth: String = MonthDateOfBirthValid,
                                 yearDateOfBirth: String = YearDateOfBirthValid,
                                 email: String = EmailValid,
                                 driverNumber: String = DriverNumberValid,
                                 postcode: String = PostcodeValid): PrivateKeeperDetailsFormModel =
    formWithValidDefaults(
      title,
      otherTitle,
      firstName,
      lastName,
      dayDateOfBirth,
      monthDateOfBirth,
      yearDateOfBirth,
      email,
      driverNumber,
      postcode
    ) match {
      case form if form.hasErrors => throw new Exception(form.errors.foldLeft("")((str, error) => str + " " + error))
      case form => form.get
    }

  private def formWithValidDefaults(title: String = "1",
                                    otherTitle: String = "",
                                    firstName: String = FirstNameValid,
                                    lastName: String = LastNameValid,
                                    dayDateOfBirth: String = DayDateOfBirthValid,
                                    monthDateOfBirth: String = MonthDateOfBirthValid,
                                    yearDateOfBirth: String = YearDateOfBirthValid,
                                    email: String = EmailValid,
                                    driverNumber: String = DriverNumberValid,
                                    postcode: String = PostcodeValid) = {

    implicit val cookieFlags = new NoCookieFlags()
    implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()
    implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")
    implicit val DateService = new DateServiceImpl()

    new PrivateKeeperDetailsTesting()
          .form.bind(
            Map(
              s"$TitleId.${TitlePickerString.TitleRadioKey}" -> title,
              s"$TitleId.${TitlePickerString.TitleTextKey}" -> otherTitle,
              FirstNameId -> firstName,
              LastNameId -> lastName,
              s"$DateOfBirthId.$DayId" -> dayDateOfBirth,
              s"$DateOfBirthId.$MonthId" -> monthDateOfBirth,
              s"$DateOfBirthId.$YearId" -> yearDateOfBirth,
              EmailId -> email,
              DriverNumberId -> driverNumber,
              PostcodeId -> postcode
            )
          )
      }
}