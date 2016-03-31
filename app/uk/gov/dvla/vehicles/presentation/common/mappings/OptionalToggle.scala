package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms._
import play.api.data.{FormError, Mapping}
import play.api.data.format.Formatter

trait OptionalToggleBase {

  final val Visible = "visible"
  final val Invisible = "invisible"
  final val OptionFieldSuffix = "-option-field"

  def binding[T](fieldMapping: Mapping[T], key: String, data: Map[String, String]): Either[Seq[FormError], Option[T]]

  def formatter[T](fieldMapping: Mapping[T]) = new Formatter[Option[T]] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[T]] =
      binding(fieldMapping, key, data)

    def unbind(key: String, value: Option[T]) = Map(
      key -> (if (value.isDefined) Visible else Invisible)
    ) ++ value.fold(Map[String, String]())(fieldMapping.unbind)
  }

  def optional[T](fieldMapping: Mapping[T]) : Mapping[Option[T]] =
    of[Option[T]](formatter[T](fieldMapping))
}

object OptionalToggle extends OptionalToggleBase {

  def binding[T](fieldMapping: Mapping[T], key: String, data: Map[String, String]) =
    data.get(key) match {
      case Some(Visible) => fieldMapping.bind(data).right.map(Some(_))
      case Some(Invisible) => Right(None)
      case _ => Left(Seq[FormError](FormError(key, "mandatory-alternative.not-selected")))
    }
}

object OptionalToggleOptional extends OptionalToggleBase {

  def binding[T](fieldMapping: Mapping[T], key: String, data: Map[String, String]) = {
    data.get(key) match {
      case Some(Invisible) => Right(None)
      case _ => fieldMapping.bind(data).right.map(Some(_)) // Some(Visible) or not selected
    }
  }
}