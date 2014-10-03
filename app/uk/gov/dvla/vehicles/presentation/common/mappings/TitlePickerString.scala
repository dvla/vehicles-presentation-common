package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

object TitlePickerString {
  final val TitleRadioKey = "titleOption"
  final val TitleTextKey = "titleText"
  final val OtherTitleRadioValue = "otherTitle"
  final val StandardOptions = List("titlePicker.mr", "titlePicker.miss", "titlePiker.mrs")

  def formatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      data.getOrElse(s"$key.$TitleRadioKey", Left(Seq[FormError](FormError(key, "error.title.unknownOption")))) match {
        case OtherTitleRadioValue => data.get(s"$key.$TitleTextKey") match {
          case Some(longTitle) if longTitle.length > 12 => Left(Seq[FormError](FormError(key, "error.title.tooLong")))
          case Some(title) => Right(title)
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
}