package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation._

object ConstraintChain {
  def apply[T](name: String,
               errorMessageSubstitute: Option[String],
               constraints: Constraint[T]*): Constraint[T] = Constraint[T](name) {
    case arg => validate(arg, errorMessageSubstitute, constraints)
  }

  private def validate[T](arg: T,
                          errorMessageSubstitute: Option[String],
                          constraints: Seq[Constraint[T]]): ValidationResult =
    if (constraints == Nil) Valid
    else constraints.head.apply(arg) match {
      case invalid: Invalid => invalid
      case Valid => validate(arg, errorMessageSubstitute, constraints.tail)
    }
}
