package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Valid}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required

object TitlePickerString {
  final val TitleRadioKey = "titleOption"
  final val TitleTextKey = "titleText"
  final val OtherTitleRadioValue = "titlePicker.other"
  final val MaxOtherTitleLength = 10
  
  def standardOptions = List("titlePicker.mr", "titlePicker.miss", "titlePicker.mrs")
  def standardOptionsMessages = standardOptions.map(Messages(_))

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
              if (title.filterNot(Character.isAlphabetic(_)).isEmpty) Right(Messages(title))
              else Left(Seq[FormError](FormError(key, "error.title.illegalCharacters")))
            case None => Left(Seq[FormError](FormError(key, "error.title.missing")))
          }
        case s: String if standardOptions.contains(s) => Right(Messages(s))
        case s: String if standardOptionsMessages.contains(s) => Right(s)
        case _ => Left(Seq[FormError](FormError(key, "error.title.unknownOption")))
      }
    }

    def unbind(key: String, value: String) = Map(
      s"$key.$TitleRadioKey" -> radioValue(value),
      s"$key.$TitleTextKey" -> textValue(value)
    )

    private def radioValue(value: String): String =
      if(standardOptions.contains(value))
        standardOptions.find(_ == value).fold(throw new Exception(""))(value => value)
      else if(standardOptionsMessages.contains(value))
        standardOptions.find(Messages(_) == value).fold(throw new Exception(""))(value => value)
      else OtherTitleRadioValue

    private def textValue(value: String): String =
      if (standardOptions.contains(value) || standardOptionsMessages.contains(value)) "" else value
  }

  def mapping = of[String](formatter).verifying(required)

  private def required = Constraint[String](Required.RequiredField) {
    case _ => Valid
  }
}
