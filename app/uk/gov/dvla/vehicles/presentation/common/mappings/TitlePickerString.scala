package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Valid}
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required

object TitlePickerString {
  final val TitleRadioKey = "titleOption"
  final val TitleTextKey = "titleText"
  final val OtherTitleRadioValue = "titlePicker.other"
  final val StandardOptions = List("titlePicker.mr", "titlePicker.miss", "titlePicker.mrs")
  private final val MaxOtherTitleLength = 12

  private type R = Either[Seq[FormError], String]

  def formatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]): R = {
      data.getOrElse(s"$key.$TitleRadioKey", Left(Seq[FormError](FormError(key, "error.title.unknownOption")))) match {
        case OtherTitleRadioValue =>
          data.get(s"$key.$TitleTextKey") match {
            case Some(longTitle) if longTitle.length > MaxOtherTitleLength =>
              Left(Seq[FormError](FormError(key, "error.title.tooLong")))
            case Some(emptyTitle) if emptyTitle.isEmpty =>
              Left(Seq[FormError](FormError(key, "error.title.missing")))
            case Some(title) =>
              if (title.filterNot(Character.isAlphabetic(_)).isEmpty) Right(title)
              else Left(Seq[FormError](FormError(key, "error.title.illegalCharacters")))
            case None => Left(Seq[FormError](FormError(key, "error.title.missing")))
          }
        case s: String if StandardOptions.contains(s) => Right[Seq[FormError], String](s)
        case _ => Left(Seq[FormError](FormError(key, "error.title.unknownOption")))
      }
    }

    def unbind(key: String, value: String) = Map(
      s"$key.$TitleRadioKey" -> s"${if(StandardOptions.contains(value)) value else OtherTitleRadioValue}",
      s"$key.$TitleTextKey" -> s"${if(StandardOptions.contains(value)) "" else value}"
    )
  }

  def mapping = of[String](formatter).verifying(required)

  private def required = Constraint[String](Required.RequiredField) {
    case _ => Valid
  }
}
