package mappings

import play.api.data.Forms.mapping
import play.api.data.{FormError, Form, Forms}
import uk.gov.dvla.vehicles.presentation.common
import common.{UnitSpec, mappings}
import common.mappings.TitlePickerString.TitleRadioKey
import common.mappings.TitlePickerString.TitleTextKey
import common.mappings.TitlePickerString.OtherTitleRadioValue
import common.mappings.TitlePickerString.formatter
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType

class TitlePickerStringSpec extends UnitSpec {
  case class TitlePickerModel(title: TitleType)

  final val form = Form(mapping(
    "title" -> Forms.of[TitleType](mappings.TitlePickerString.formatter)
  )(TitlePickerModel.apply)(TitlePickerModel.unapply))

  "Binding a title picker string" should {

    "Pick any of the standard options" in {

      def testBindStandard(option: Int) = form.bind(Map(
        s"title.$TitleRadioKey" -> option.toString,
        s"title.$TitleTextKey" -> "someRandomText"
      )).value should equal(Some(TitlePickerModel(TitleType(option, ""))))

      testBindStandard(1)
      testBindStandard(2)
      testBindStandard(3)
    }

    "Pick the other option value" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString,
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Right(TitleType(4, "smthelse")))
    }

    "Reject title values not in the range 1, 2, 3, 4" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> "5",
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))

      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> "0",
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))

      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> "sdfsd",
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))
    }

    "Pick the other option no value" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString,
        s"key1.$TitleTextKey" -> ""
      )) should equal(Left(Seq(FormError("key1", "error.title.missing"))))

      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString
      )) should equal(Left(Seq(FormError("key1", "error.title.missing"))))
    }

    "Validate other option value length" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString,
        s"key1.$TitleTextKey" -> "A long other option value"
      )) should equal(Left(Seq(FormError("key1", "error.title.tooLong"))))
    }

    "Validate other option value incorrect characters" in {
      def validate(other: String) = formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString,
        s"key1.$TitleTextKey" -> other
      )) should equal(Left(Seq(FormError("key1", "error.title.illegalCharacters"))))

      validate(" ")
      validate("  ")
      validate("sdf123sdf")
      validate("sdf!sdf")
      validate("sf!@#%^")
    }

    "Validate some random option" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> "someOtherOptionValue"
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))
    }

    "Validate some no option" in {
      formatter.bind("key1", Map(
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))
    }
  }

  "Unbind a title" should {
    "Set any of the standard options" in {
      formatter.unbind("key1", TitleType(1, "")) should equal(
        Map(s"key1.$TitleRadioKey" -> "1", s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", TitleType(2, "")) should equal(
        Map(s"key1.$TitleRadioKey" -> "2", s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", TitleType(3, "")) should equal(
        Map(s"key1.$TitleRadioKey" -> "3", s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", TitleType(3, "sdfsdf")) should equal(
        Map(s"key1.$TitleRadioKey" -> "3", s"key1.$TitleTextKey" -> "")
      )
    }

    "Set the other option value" in {
      formatter.unbind("key1", TitleType(OtherTitleRadioValue, "smthelse")) should equal(
        Map(s"key1.$TitleRadioKey" -> OtherTitleRadioValue.toString, s"key1.$TitleTextKey" -> "smthelse")
      )
    }

    "Not set anything if the title type is not 1, 2, 3 or 4" in {
      formatter.unbind("key1", TitleType(0, "smthelse")) should equal(Map())
      formatter.unbind("key1", TitleType(5, "smthelse")) should equal(Map())
    }
  }
}
