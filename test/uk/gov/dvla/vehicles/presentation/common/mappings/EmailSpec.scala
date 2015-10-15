package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import uk.gov.dvla.vehicles.presentation.common.UnitSpec

class EmailSpec extends UnitSpec {
  private val formatter = Email.formatter()

  "bind" should {
    "pass a emails with the same value" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "abc@xyz.com",
        s"email1.${Email.EmailVerifyId}" -> "abc@xyz.com"
      )) should equal(Right("abc@xyz.com"))
    }

    "pass a emails with the same value, but different case" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "ABC@XYZ.COM",
        s"email1.${Email.EmailVerifyId}" -> "abc@xyz.com"
      )) should equal(Right("abc@xyz.com"))
    }

    "generate error for both fields missing" in {
      formatter.bind("email1", Map()) should equal(Left(Seq(FormError(s"email1", "error.email"))))
    }

    "generate error for empty email field" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> ""
      )) should equal(Left(Seq(FormError(s"email1", "error.email"))))
    }

    "generate error for emails with different values" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "abc@xyz.com",
        s"email1.${Email.EmailVerifyId}" -> "xyz@abc.com"
      )) should equal(Left(Seq(FormError(s"email1", "error.email.not.match"))))
    }

    "generate error for missing email field" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailVerifyId}" -> "xyz@abc.com"
      )) should equal(Left(Seq(FormError(s"email1", "error.email.not.match"))))
    }

    "generate error for empty email confirm field" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "abc@xyz.com",
        s"email1.${Email.EmailVerifyId}" -> ""
      )) should equal(Left(Seq(FormError(s"email1", "error.email.confirm.required"))))
    }

    "generate error for email confirm field blak spaces only" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "abc@xyz.com",
        s"email1.${Email.EmailVerifyId}" -> "\t   \n"
      )) should equal(Left(Seq(FormError(s"email1", "error.email.confirm.required"))))
    }

    "generate error for missing email confirm field" in {
      formatter.bind("email1", Map(
        s"email1.${Email.EmailId}" -> "xyz@abc.com"
      )) should equal(Left(Seq(FormError(s"email1", "error.email.confirm.required"))))
    }

    "generate error for missing both email fields" in {
      formatter.bind("email1", Map(
      )) should equal(Left(Seq(FormError(s"email1", "error.email"))))
    }
  }

  "unbind should populate both fields" in {
    formatter.unbind("email1", "abc@xyz.com") should equal(Map(
      s"email1.${Email.EmailId}" -> "abc@xyz.com",
      s"email1.${Email.EmailVerifyId}" -> "abc@xyz.com"
    ))
  }
}
