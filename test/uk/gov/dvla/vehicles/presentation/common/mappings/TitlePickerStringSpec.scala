package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.mapping
import play.api.data.{Form, FormError, Forms}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.mappings.TitlePickerString.{OtherTitleRadioValue, StandardOptions, TitleRadioKey, TitleTextKey, formatter}
import common.{UnitSpec, mappings}

class TitlePickerStringSpec extends UnitSpec {
  case class TitlePickerModel(title: String)

  final val form = Form(mapping(
    "title" -> Forms.of[String](mappings.TitlePickerString.formatter)
  )(TitlePickerModel.apply)(TitlePickerModel.unapply))

  "Binding a title picker string" should {

    "Pick any of the standard options" in {
      def testBindStandard(option: String) = form.bind(Map(
        s"title.$TitleRadioKey" -> option,
        s"title.$TitleTextKey" -> "someRandomText"
      )).value should equal(Some(TitlePickerModel(Messages(option))))

      testBindStandard(StandardOptions(0))
      testBindStandard(StandardOptions(1))
      testBindStandard(StandardOptions(2))
    }

    "Pick the other option value" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> "smthelse"
      )) should equal(Right("smthelse"))
    }

    "Validate other option value length" in {
      formatter.bind("key1", Map(
        s"key1.$TitleRadioKey" -> OtherTitleRadioValue,
        s"key1.$TitleTextKey" -> "A long other option value"
      )) should equal(Left(Seq(FormError("key1", "error.title.tooLong"))))
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
      formatter.unbind("key1", StandardOptions(0)) should equal(
        Map(s"key1.$TitleRadioKey" -> StandardOptions(0), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", StandardOptions(1)) should equal(
        Map(s"key1.$TitleRadioKey" -> StandardOptions(1), s"key1.$TitleTextKey" -> "")
      )
      formatter.unbind("key1", StandardOptions(2)) should equal(
        Map(s"key1.$TitleRadioKey" -> StandardOptions(2), s"key1.$TitleTextKey" -> "")
      )
    }

    "Set the other option value" in {
      formatter.unbind("key1", "smthelse") should equal(
        Map(s"key1.$TitleRadioKey" -> OtherTitleRadioValue, s"key1.$TitleTextKey" -> "smthelse")
      )
    }
  }
}
