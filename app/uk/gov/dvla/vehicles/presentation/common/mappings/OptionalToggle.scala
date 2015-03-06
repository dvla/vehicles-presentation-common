package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms._
import play.api.data.{FormError, Mapping}
import play.api.data.format.Formatter

object OptionalToggle {
  final val Visible = "visible"
  final val Invisible = "invisible"

  def formatter[T](fieldMapping: Mapping[T]) = new Formatter[Option[T]] {
    type R = Either[Seq[FormError], Option[T]]

    override def bind(key: String, data: Map[String, String]): R =
      data.get(key) match {
        case Some(Visible) => fieldMapping.bind(data).right.map(Some(_))
        case Some(Invisible) => Right(None)
        case _ =>  Left(Seq[FormError](FormError(key, "mandatory-alternative.not-selected")))
      }

    def unbind(key: String, value: Option[T]) = Map(
      key -> {if (value.isDefined) Visible else Invisible}
    ) ++ value.fold(Map[String, String]())(fieldMapping.unbind(_))
  }

  def optional[T](fieldMapping: Mapping[T]) : Mapping[Option[T]] =
    of[Option[T]](formatter[T](fieldMapping))
}
