package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{ValidationError, Valid, Invalid, Constraint}
import play.api.data.validation.Constraints.pattern

import scala.annotation.tailrec

object BusinessName {

  final val Pattern = """^[a-zA-Z0-9][a-zA-Z0-9\s\-\'\"\,\&\/\(\)\.]*$"""
  final val OneCharPattern = """[a-zA-Z]{1,}"""

  def validBusinessName: Constraint[String] = nameConstrain(
    "constraint.validBusinessName",
    "error.validBusinessName",
    "error.invalidBraces",
    "error.oddNumberOfQuotes"
  )

  def nameConstrain(name: String,
                    charactersError: String,
                    bracesError: String,
                    quotesError: String): Constraint[String] =
    Constraint[String](name) {
      case str: String =>
        pattern(Pattern.r, name, charactersError).apply(str) match {
          case invalid: Invalid => invalid
          case Valid if balanceCount(str) != 0 => Invalid(ValidationError(bracesError))
          case Valid if hasOddNumberOfQuotes(str) => Invalid(ValidationError(quotesError))
          case _ => Valid
        }
    }

  private def hasOddNumberOfQuotes(str: String) =
    str.count( _ == '"') % 2 != 0

  @tailrec
  private def balanceCount(input: String, count: Int = 0, openChar: Char= '(', closeChar: Char = ')'): Int =
    if (input.isEmpty) count
    else if (count < 0) count
    else input.head match {
      case `openChar` => balanceCount(input.tail, count + 1, openChar, closeChar)
      case `closeChar` => balanceCount(input.tail, count - 1, openChar, closeChar)
      case _ => balanceCount(input.tail, count, openChar, closeChar)
    }
}
