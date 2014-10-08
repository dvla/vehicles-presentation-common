package mappings

import helpers.WithApplication
import play.api.data.Forms.mapping
import play.api.data.{FormError, Form, Forms}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common
import common.{UnitSpec, mappings}
import common.mappings.TitlePickerString.TitleRadioKey
import common.mappings.TitlePickerString.TitleTextKey
import common.mappings.TitlePickerString.OtherTitleRadioValue
import common.mappings.TitlePickerString.formatter
import common.mappings.TitlePickerString.standardOptions
import common.mappings.TitlePickerString.standardOptionsMessages

class TitlePickerStringSpec extends UnitSpec {
  case class TitlePickerModel(title: String)

  final val form = Form(mapping(
    "title" -> Forms.of[String](mappings.TitlePickerString.formatter)
  )(TitlePickerModel.apply)(TitlePickerModel.unapply))

  "Binding a title picker string" should {

    "Pick any of the standard options" in new WithApplication {

      def testBindStandard(option: String) = form.bind(Map(
        s"title.$TitleRadioKey" -> option,
        s"title.$TitleTextKey" -> "someRandomText"
      )).value should equal(Some(TitlePickerModel(Messages(option))))

      testBindStandard(standardOptions(0))
      testBindStandard(standardOptions(1))
      testBindStandard(standardOptions(2))
    }

    "Pick the other option value" in new WithApplication {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Right("smthelse"))
    }

    "Pick the other option no value" in new WithApplication {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> ""
      )) should equal(Left(Seq(FormError("key1", "error.title.missing"))))

      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue
      )) should equal(Left(Seq(FormError("key1", "error.title.missing"))))
    }

    "Validate other option value length" in new WithApplication {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> "A long other option value"
      )) should equal(Left(Seq(FormError("key1", "error.title.tooLong"))))
    }

    "Validate other option value incorrect characters" in new WithApplication {
      def validate(other: String) = formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> other
      )) should equal(Left(Seq(FormError("key1", "error.title.illegalCharacters"))))

      validate(" ")
      validate("  ")
      validate("sdf123sdf")
      validate("sdf!sdf")
      validate("sf!@#%^")
    }

    "Validate some random option" in new WithApplication {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> "someOtherOptionValue"
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))
    }

    "Validate some no option" in new WithApplication {
      formatter.bind("key1", Map(
      )) should equal(Left(Seq(FormError("key1", "error.title.unknownOption"))))
    }
  }

  "Unbind a title" should {
    "Set any of the standard options" in new WithApplication {
      formatter.unbind("key1", standardOptions(0)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(0), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", standardOptions(1)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(1), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", standardOptions(2)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(2), s"key1.$TitleTextKey" -> "")
      )
    }

    "Set any of the standard option values" in new WithApplication {
      formatter.unbind("key1", standardOptionsMessages(0)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(0), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", standardOptionsMessages(1)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(1), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", standardOptionsMessages(2)) should equal(
        Map(s"key1.$TitleRadioKey" -> standardOptions(2), s"key1.$TitleTextKey" -> "")
      )
    }

    "Set the other option value" in new WithApplication {
      formatter.unbind("key1", "smthelse") should equal(
        Map(s"key1.$TitleRadioKey" -> OtherTitleRadioValue, s"key1.$TitleTextKey" -> "smthelse")
      )
    }
  }
}
