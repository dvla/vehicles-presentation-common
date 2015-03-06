package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms._
import play.api.data.{FormError, Mapping}
import play.api.data.format.Formatter

object OptionalToggle {
  final val Yes = "yes"
  final val No = "no"

  def formatter[T](optionKey: String, fieldMapping: Mapping[T]) = new Formatter[Option[T]] {
    type R = Either[Seq[FormError], Option[T]]

    override def bind(key: String, data: Map[String, String]): R =
      data.get(optionKey) match {
        case Some(Yes) => fieldMapping.bind(data).right.map(Some(_))
        case Some(No) => Right(None)
        case _ =>  Left(Seq[FormError](FormError(s"radio-button-for-key$key-was-not-set", "")))
      }

    def unbind(key: String, value: Option[T]) = Map(
      optionKey -> {if (value.isDefined) Yes else No}
    ) ++ value.fold(Map[String, String]())(fieldMapping.unbind(_))
  }

  def optional[T](optionKey: String, fieldMapping: Mapping[T]) : Mapping[Option[T]] =
    of[Option[T]](formatter[T](optionKey, fieldMapping))
}
