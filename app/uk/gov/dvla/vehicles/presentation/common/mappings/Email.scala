package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Forms.of
import play.api.data.{FormError, Mapping}
import play.api.data.format.Formatter
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Email.emailAddress
import play.api.data.format.Formats._

object Email {
  final val EmailId = "email"
  final val EmailVerifyId = "email-verify"

  final val EmailMinLength = 3
  final val EmailMaxLength = 254
  final val EmailUsernameMaxLength = 64
  final val EmailDomainSectionMaxLength = 63
  final val InvalidUsernameChar = "\"."
  final val InvalidDomainStartEndChar = "-"
  final val InvalidDomainContentChar = "/"

  def formatter() = new Formatter[String] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      (data.get(s"$key.${Email.EmailId}"), data.get(s"$key.${Email.EmailVerifyId}")) match {
        case (None, None) => Left(Seq(FormError(key, "error.email")))
        case (_, None) => Left(Seq(FormError(key, "error.email.confirm.required")))
        case (_, Some(emailConfirm)) if emailConfirm.trim.isEmpty =>
          Left(Seq(FormError(key, "error.email.confirm.required")))
        case (Some(email), Some(emailConfirm)) if email == emailConfirm => Right(email)
        case _ =>  Left(Seq(FormError(key, "error.email.not.match")))
      }

    override def unbind(key: String, value: String): Map[String, String] = Map(
      Email.EmailId -> value,
      Email.EmailVerifyId -> value
    )
  }

  def email: Mapping[String] = of[String] verifying emailAddress

  def emailConfirm: Mapping[String] = of[String](formatter()) verifying emailAddress
}
