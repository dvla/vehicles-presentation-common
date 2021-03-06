package uk.gov.dvla.vehicles.presentation.common.controllers.k2kacquire

import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import common.model.CacheKeyPrefix
import common.model.NewKeeperEnterAddressManuallyFormModel.Form.AddressAndPostcodeId
import common.services.DateServiceImpl
import common.{UnitSpec, TestWithApplication}
import common.views.models.AddressLinesViewModel.Form.AddressLinesId
import common.views.models.AddressLinesViewModel.Form.BuildingNameOrNumberId
import common.views.models.AddressLinesViewModel.Form.Line2Id
import common.views.models.AddressLinesViewModel.Form.Line3Id
import common.views.models.AddressLinesViewModel.Form.LineMaxLength
import common.views.models.AddressLinesViewModel.Form.TownMaxLength
import common.views.models.AddressLinesViewModel.Form.PostTownId
import common.views.models.AddressAndPostcodeViewModel.Form.PostcodeId

class NewKeeperEnterAddressManuallyFormSpec extends UnitSpec {

  final val BuildingNameOrNumberValid = "123ABC"
  final val Line2Valid = "line2 stub"
  final val Line3Valid = "line3 stub"
  final val PostTownValid = "postTown stub"
  final val PostCodeValid = "AA11AA"

  "form" should {
    "accept if form is valid with all fields filled in" in new TestWithApplication {
      val model = formWithValidDefaults().get.addressAndPostcodeModel

      model.addressLinesModel.buildingNameOrNumber should equal(BuildingNameOrNumberValid.toUpperCase)
      model.addressLinesModel.line2 should equal(Some(Line2Valid.toUpperCase))
      model.addressLinesModel.line3 should equal(Some(Line3Valid.toUpperCase))
      model.addressLinesModel.postTown should equal(PostTownValid.toUpperCase)
    }

    "accept if form is valid with only mandatory filled in" in new TestWithApplication {
      val model = formWithValidDefaults(line2 = "", line3 = "").get.addressAndPostcodeModel

      model.addressLinesModel.buildingNameOrNumber should equal(BuildingNameOrNumberValid.toUpperCase)
    }
  }

  "address lines" should {
    "accept if form address lines contain hyphens" in new TestWithApplication {
      val model = formWithValidDefaults(buildingNameOrNumber = buildingNameOrNumberHypthens,
        line2 = line2Hypthens,line3 = line3Hypthens, postTown = postTownHypthens)
        .get.addressAndPostcodeModel

      model.addressLinesModel.buildingNameOrNumber should equal(buildingNameOrNumberHypthens.toUpperCase)
      model.addressLinesModel.line2 should equal(Some(line2Hypthens.toUpperCase))
      model.addressLinesModel.line3 should equal(Some(line3Hypthens.toUpperCase))
      model.addressLinesModel.postTown should equal(postTownHypthens.toUpperCase)
    }

    "reject when all fields are blank" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "", line2 = "", line3 = "", postTown = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.minLength", "error.required")
    }

    "reject if post town is blank" in new TestWithApplication {
      formWithValidDefaults(postTown = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required")
    }

    "reject if post town contains numbers" in new TestWithApplication {
      formWithValidDefaults(postTown = "123456").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.address.postTown.characterInvalid")
    }

    "accept if post town starts with spaces" in new TestWithApplication {
      formWithValidDefaults(postTown = " Swansea").get.addressAndPostcodeModel.addressLinesModel.
        postTown should equal("SWANSEA")
    }

    "reject if buildingNameOrNumber is blank" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required")
    }

    "reject if buildingNameOrNumber is less than min length" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "abc", line2 = "", line3 = "", postTown = PostTownValid)
        .errors.flatMap(_.messages) should contain theSameElementsAs List("error.minLength")
    }

    "reject if buildingNameOrNumber is more than max length" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "a" * (LineMaxLength + 1),
        line2 = "", line3 = "", postTown = PostTownValid).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if buildingNameOrNumber is greater than max length" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "a" * (LineMaxLength + 1)).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if buildingNameOrNumber contains special characters" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "The*House").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.address.characterInvalid")
    }

    "reject if line2 is more than max length" in new TestWithApplication {
      formWithValidDefaults(line2 = "a" * (LineMaxLength + 1),
        line3 = "", postTown = PostTownValid).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if line3 is more than max length" in new TestWithApplication {
      formWithValidDefaults(line2 = "", line3 = "a" * (LineMaxLength + 1), postTown = PostTownValid).
        errors.flatMap(_.messages) should contain theSameElementsAs List("error.maxLength")
    }

    "reject if postTown is more than max length" in new TestWithApplication {
      formWithValidDefaults(line2 = "", line3 = "", postTown = "a" * (LineMaxLength + 1)).
        errors.flatMap(_.messages) should contain theSameElementsAs List("error.maxLength")
    }

    "reject if postTown is less than min length" in new TestWithApplication {
      formWithValidDefaults(line2 = "", line3 = "", postTown = "ab").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if total length of all address lines is exceeded" in new TestWithApplication {
      formWithValidDefaults(
        buildingNameOrNumber = "a" * LineMaxLength + 1,
        line2 = "b" * LineMaxLength,
        line3 = "c" * LineMaxLength,
        postTown = "d" * TownMaxLength
      ).errors should have length 1
    }

    "reject if any line contains html chevrons" in new TestWithApplication {
      formWithValidDefaults(buildingNameOrNumber = "A<br>B").errors should have length 1
      formWithValidDefaults(line2 = "A<br>B").errors should have length 1
      formWithValidDefaults(line3 = "A<br>B").errors should have length 1
      formWithValidDefaults(postTown = "A<br>B").errors should have length 1
    }
  }

  private def formWithValidDefaults(buildingNameOrNumber: String = BuildingNameOrNumberValid,
                                    line2: String = Line2Valid,
                                    line3: String = Line3Valid,
                                    postTown: String = PostTownValid,
                                    postCode: String = PostCodeValid) = {

    implicit val cookieFlags = new NoCookieFlags()
    implicit val sideSessionFactory = new ClearTextClientSideSessionFactory()
    implicit val cacheKeyPrefix = CacheKeyPrefix("testing-prefix")
    implicit val DateService = new DateServiceImpl()

    new NewKeeperEnterAddressManuallyTesting().
    form.bind(
      Map(
        s"$AddressAndPostcodeId.$AddressLinesId.$BuildingNameOrNumberId" -> buildingNameOrNumber,
        s"$AddressAndPostcodeId.$AddressLinesId.$Line2Id" -> line2,
        s"$AddressAndPostcodeId.$AddressLinesId.$Line3Id" -> line3,
        s"$AddressAndPostcodeId.$AddressLinesId.$PostTownId" -> postTown,
        s"$AddressAndPostcodeId.$PostcodeId" -> postCode
      )
    )
  }

  val buildingNameOrNumberHypthens = "1-12ABC"
  val line2Hypthens = "address line - 2"
  val line3Hypthens = "address line - 3"
  val postTownHypthens = "address-line"
}