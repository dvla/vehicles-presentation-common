package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraints._
import play.api.data.validation._

import scala.annotation.tailrec

object BusinessKeeperName {
  final val GeneralError = "error.validBusinessKeeperName"
  final val BracesOrQuotesError = "error.invalidBracesOrQuotes"

  final val Pattern = """^[a-zA-Z0-9][a-zA-Z0-9\s\-\'\"\,\&\/\(\)\.]*$"""

  def validBusinessKeeperName: Constraint[String] = nameConstrain(
    "constraint.validBusinessKeeperName",
    GeneralError,
    BracesOrQuotesError
  )

  def atLeastACharacter: Constraint[String] = Constraint[String]("constraint.atLeastOneChar") {
    case str: String =>
      if (str.replaceAll( """[^A-Za-z]""", "").length < 1)
        Invalid(ValidationError("error.atLeastOneChar"))
      else
        Valid
  }

  def nameConstrain(name: String,
                    charactersError: String,
                    bracesOrQuotesError: String): Constraint[String] =
    ConstraintChain[String](name, None,
      pattern(Pattern.r, name, charactersError),
      Constraint[String](name) { case str: String =>
        if (balanceCount(str) != 0) Invalid(ValidationError(bracesOrQuotesError))
        else if (hasOddNumberOfQuotes(str)) Invalid(ValidationError(bracesOrQuotesError))
        else Valid
      })

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
