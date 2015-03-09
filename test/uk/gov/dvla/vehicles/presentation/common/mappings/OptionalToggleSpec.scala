package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.Forms._
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle.optional

class OptionalToggleSpec extends UnitSpec {
  case class OptionalToggleTestModel(text: Option[String])
  val m = mapping(
    "option-id" -> optional(nonEmptyText(0, 10).withPrefix("text-id"))
  )(OptionalToggleTestModel.apply)(OptionalToggleTestModel.unapply)

  "Formatter bind" should {
    "Report errors if non of the options is selected" in {
      m.bind(Map("text-id" -> "some text")) should equal(Left(Seq(FormError("option-id", "mandatory-alternative.not-selected"))))
    }

    "Return None if the Invisible option is selected without doing the underlying mapping" in {
      m.bind(Map(
        "option-id" -> OptionalToggle.Invisible,
        "text-id" -> "some text")
      ) should equal(Right(OptionalToggleTestModel(None)))
    }

    "Perform the underlying mapping if the Visible option is selected and return Some with it's successful result" in {
      m.bind(Map(
        "option-id" -> OptionalToggle.Visible,
        "text-id" -> "some text"
      )) should equal(Right(OptionalToggleTestModel(Some("some text"))))
    }

    "Perform the underlying mapping if the Visible option is selected and return the errors generated" in {
      m.bind(Map(
        "option-id" -> OptionalToggle.Visible
      )) should equal(Left(Seq(FormError("text-id", "error.required"))))
    }
  }

  "Formatter unbind" should {
    "Populate the map with invisible on None" in {
      m.unbind(OptionalToggleTestModel(None)) should equal(Map("option-id" -> OptionalToggle.Invisible))
    }

    "Populate the map with the underlying mapping on Some()" in {
      m.unbind(OptionalToggleTestModel(Some("test string"))) should equal(Map(
        "option-id" -> OptionalToggle.Visible,
        "text-id" -> "test string"
      ))
    }
  }
}
