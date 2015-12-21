package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object Postcode {
  val regex =
    """^
      |(?i)(GIR 0AA)|
      |((([A-Z][0-9][0-9]?)|
      |(([A-Z][A-HJ-Y][0-9][0-9]?)|
      |(([A-Z][0-9][A-Z])|
      |([A-Z][A-HJ-Y][0-9]?[A-Z]))))[ ]?[0-9][A-Z]{2})
      |$""".stripMargin.replace("\n", "").r

  def validPostcode: Constraint[String] = pattern(
    regex = regex,
    name = "constraint.restricted.validPostcode",
    error = "error.restricted.validPostcode"
  )

  // TODO I think this should move out of the constraint as it is not a constraint, it re-formats for viewing. It could live in a model but we don't yet have a model for postcode
  def formatPostcode(postcode: String) = {
    val SpaceCharDelimiter = " "
    val postCodeFormatter = """(?i)^([A-Z]{1,2})[ 0]*([0-9]+[A-Z]?) ?([0-9][A-Z]{1,2})$""".r

    postcode match {
      case postCodeFormatter(area, district, inward) => s"$area$district$SpaceCharDelimiter$inward"
      case _ => postcode.replaceAll("\\*", " ") // partial postcodes include asterisks so replace with spaces
    }
  }
}