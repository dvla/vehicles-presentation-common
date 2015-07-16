package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{Valid, ValidationError, Invalid, Constraint}
import play.api.data.validation.Constraints.pattern
import BusinessName.{Pattern, OneCharPattern}

import scala.annotation.tailrec

object BusinessKeeperName {

  def validBusinessKeeperName: Constraint[String] = {
    val name = "constraint.validBusinessKeeperName"
    Constraint[String](name) {
      case str: String =>
        pattern(Pattern.r, name, "error.validBusinessKeeperName").apply(str) match {
          case invalid: Invalid => invalid
          case Valid if balanceCount(str) != 0 => Invalid(ValidationError("error.invalidBraces"))
          case Valid if hasEvenNumberOfQuotes(str) => Invalid(ValidationError("error.oddNumberOfQuotes"))
          case _ => Valid
        }
    }
  }


  def atLeastACharacter: Constraint[String] = Constraint[String]("constraint.atLeastOneChar") {
    case str: String =>
      if (str.replaceAll( """[^A-Za-z]""", "").length < 1)
        Invalid(ValidationError("error.atLeastOneChar"))
      else
        Valid
  }

  private def hasEvenNumberOfQuotes(str: String) =
    str.count( _ == '"') % 2 == 0

//  private def isBalancedString(input: String, openChar: Char = '(', closeChar: Char = ')'): Boolean =
//    input.foldLeft(0){ case (count, char) =>
//      if (count < 0) return false
//      else char match {
//        case `openChar` => count + 1
//        case `closeChar` => count - 1
//        case _ => count
//      }
//    } >= 0

  @tailrec
  def balanceCount(input: String, count: Int = 0, openChar: Char= '(', closeChar: Char = ')'): Int =
    if (input == Nil) count
    else input.head match {
      case `openChar` => balanceCount(input.tail, count + 1, openChar, closeChar)
      case `closeChar` => balanceCount(input.tail, count - 1, openChar, closeChar)
      case _ => balanceCount(input.tail, count, openChar, closeChar)
    }
}
