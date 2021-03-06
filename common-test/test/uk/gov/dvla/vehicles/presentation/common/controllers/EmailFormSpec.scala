package uk.gov.dvla.vehicles.presentation.common.controllers

import uk.gov.dvla.vehicles.presentation.common
import common.helpers.TestWithApplication
import common.helpers.UnitSpec
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.models

import models.EmailModel.Form.EmailId

class EmailFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with email name field filled in" in new TestWithApplication {
      val model = formWithValidDefaults().get
      model.email should equal(Some(EmailValid))
    }
  }

  "email" should {
    "accept with no entry" in new TestWithApplication {
      val model = formWithValidDefaults(email = "").get
      model.email should equal(None)
    }

    "reject if incorrect format" in new TestWithApplication {
      formWithValidDefaults(email = "no_at_symbol.com").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if less than min length" in new TestWithApplication {
      formWithValidDefaults(email = "no").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if greater than max length" in new TestWithApplication {
      formWithValidDefaults(email = "n@" + ("a" * 62) + "." + ("b" * 62) + "." + ("c" * 62) + "." + ("d" * 62) + ".co.uk")
        .errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }
  }

  private final val EmailValid = "my@email.com"
  private def formWithValidDefaults(email: String = EmailValid) = {

    injector.getInstance(classOf[EmailController])
      .form.bind(
        Map(
          s"$EmailId.$EmailEnterId" -> email,
          s"$EmailId.$EmailVerifyId" -> email
        )
      )
  }
}
