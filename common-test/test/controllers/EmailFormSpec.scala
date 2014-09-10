package controllers

import models.EmailModel.Form.EmailId
import helpers.UnitSpec

final class EmailFormSpec extends UnitSpec {

  "form" should {
    "accept if form is valid with email name field filled in" in {
      val model = formWithValidDefaults().get
      model.email should equal(Some(EmailValid))
    }
  }

  "email" should {
    "accept with no entry" in {
      val model = formWithValidDefaults(email = "").get
      model.email should equal(None)
    }

    "reject if incorrect format" in {
      formWithValidDefaults(email = "no_at_symbol.com").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if less than min length" in {
      formWithValidDefaults(email = "no").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if greater than max length" in {
      formWithValidDefaults(email = "n@" + ("a" * 62) + "." + ("b" * 62) + "." + ("c" * 62) + "." + ("d" * 62) + ".co.uk")
        .errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }
  }

  private final val EmailValid = "my@email.com"
  private def formWithValidDefaults(email: String = EmailValid) = {

    injector.getInstance(classOf[EmailController])
      .form.bind(
        Map(EmailId -> email)
      )
  }
}
