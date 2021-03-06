package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.Forms.of
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Valid}
import play.api.i18n.Messages
import scala.util.Try
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Required

object TitlePickerString {
  final val TitleRadioKey = "titleOption"
  final val TitleTextKey = "titleText"
  final val OtherTitleRadioValue = 4
  final val MaxOtherTitleLength = 10
  
  def standardOptions = List("titlePicker.mr", "titlePicker.mrs", "titlePicker.miss")
  def standardOptionsMessages = standardOptions.map(Messages(_))

  private type R = Either[Seq[FormError], TitleType]

  def formatter = new Formatter[TitleType] {
    def bind(key: String, data: Map[String, String]): R = {
      data.getOrElse(s"$key.$TitleRadioKey", constructError(key, "error.title.unknownOption")) match {
        case s: String if s == OtherTitleRadioValue.toString =>
          data.get(s"$key.$TitleTextKey").map(removeWhiteSpacesFromMiddle) match {
            case Some(longTitle) if longTitle.length > MaxOtherTitleLength =>
              constructError(key, "error.title.tooLong")
            case Some(emptyTitle) if emptyTitle.isEmpty =>
              constructError(key, "error.title.missing")
            case Some(title) =>
              val regEx = """^[a-zA-Z\s\-\.\']{1,}$""".r
              if (regEx.pattern.matcher(title).matches()) constructSuccess(OtherTitleRadioValue, title)
              else constructError(key, "error.title.illegalCharacters")
            case None => constructError(key, "error.title.missing")
          }
        case s: String if isWithinStandard(s) => constructSuccess(s.toInt, "")
        case _ => constructError(key, "error.title.unknownOption")
      }
    }

    /**
     * Removes all the extra whitespaces.
     * @param text the text to process
     * @return a text with all the extra whitespaces stripped.
     */
    private def removeWhiteSpacesFromMiddle(text: String): String =
        text.split(" ").filter("" != _).mkString(" ")

    private def constructError(key: String, errorKey: String) : R =
      Left(Seq[FormError](FormError(key, errorKey)))

    private def constructSuccess(titleType: Int, otherText: String) : R =
      Right(TitleType(titleType, otherText))

    def unbind(key: String, value: TitleType) =
      if (isWithin(value.titleType)) Map(
        s"$key.$TitleRadioKey" -> radioValue(value),
        s"$key.$TitleTextKey" -> textValue(value)
      ) else Map()

    private def isWithinStandard(t: String): Boolean =
      Try(t.toInt).toOption.fold(false)(t => isWithinStandard(t))

    private def isWithinStandard(t: Int): Boolean = t > 0 && t <= standardOptions.length

    private def isWithin(t: Int) = isWithinStandard(t) || t == OtherTitleRadioValue

    private def radioValue(value: TitleType): String =
      if (isWithin(value.titleType)) value.titleType.toString
      else "-1"

    private def textValue(value: TitleType): String =
      if (OtherTitleRadioValue == value.titleType) value.other else ""
  }

  def mapping = of[TitleType](formatter).verifying(required)

  private def required = Constraint[TitleType](Required.RequiredField) {
    case _ => Valid
  }
}

case class TitleType(titleType: Int, other: String)
