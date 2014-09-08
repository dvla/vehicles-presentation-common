package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object RegistrationNumber {

  def validRegistrationNumber: Constraint[String] = pattern(
    regex =
      """^
        |([A-Za-z]{3}[0-9]{1,4})|
        |([A-Za-z][0-9]{1,3}[A-Za-z]{3})|
        |([A-Za-z]{3}[0-9]{1,3}[A-Za-z])|
        |([A-Za-z]{2}[0-9]{2}[A-Za-z]{3})|
        |([A-Za-z]{1,3}[0-9]{1,3})|
        |([0-9]{1,4}[A-Za-z]{1,3})|
        |([A-Za-z]{1,2}[0-9]{1,4})
        |$""".stripMargin.replace("\n", "").r,
    name = "constraint.restricted.validVrn",
    error = "error.restricted.validVrnOnly"
  )

  def formatVrm(vrm: String): String = {
    val FiveSpaceCharPadding = " " * 5
    val FourSpaceCharPadding = " " * 4
    val ThreeSpaceCharPadding = " " * 3
    val TwoSpaceCharPadding = " " * 2
    val OneSpaceCharPadding = " " * 1

    val AA99AAA = "([A-Z]{2}[0-9]{2}[A-Z]{3})".r
    val A9 = "([A-Z][0-9])".r
    val A99 = "([A-Z][0-9]{2})".r
    val A999 = "([A-Z][0-9]{3})".r
    val A9999 = "([A-Z][0-9]{4})".r
    val AA9 = "([A-Z]{2}[0-9])".r
    val AA99 = "([A-Z]{2}[0-9]{2})".r
    val AA999 = "([A-Z]{2}[0-9]{3})".r
    val AA9999 = "([A-Z]{2}[0-9]{4})".r
    val AAA9 = "([A-Z]{3}[0-9])".r
    val AAA99 = "([A-Z]{3}[0-9]{2})".r
    val AAA999 = "([A-Z]{3}[0-9]{3})".r
    val AAA9999 = "([A-Z]{3}[0-9]{4})".r
    val AAA9Y = "([A-Z]{3}[0-9][A-Z])".r
    val AAA99Y = "([A-Z]{3}[0-9]{2}[A-Z])".r
    val AAA999Y = "([A-Z]{3}[0-9]{3}[A-Z])".r
    val `9A` = "([0-9][A-Z])".r
    val `0009  A` = "([0]{3}[0-9][A-Z])".r
    val `9AA` = "([0-9][A-Z]{2})".r
    val `0009 AA` = "([0]{3}[0-9][A-Z]{2})".r
    val `9AAA` = "([0-9][A-Z]{3})".r
    val `0009AAA` = "([0]{3}[0-9][A-Z]{3})".r
    val `99A` = "([0-9]{2}[A-Z])".r
    val `0099  A` = "([0]{2}[0-9]{2}[A-Z])".r
    val `99AA` = "([0-9]{2}[A-Z]{2})".r
    val `0099 AA` = "([0]{2}[0-9]{2}[A-Z]{2})".r
    val `99AAA` = "([0-9]{2}[A-Z]{3})".r
    val `0099AAA` = "([0]{2}[0-9]{2}[A-Z]{3})".r
    val `999A` = "([0-9]{3}[A-Z])".r
    val `0999  A` = "([0][0-9]{3}[A-Z])".r
    val `999AA` = "([0-9]{3}[A-Z]{2})".r
    val `0999 AA` = "([0][0-9]{3}[A-Z]{2})".r
    val `9999AAA` = "([0-9]{3}[A-Z]{3})".r
    val `0999AAA` = "([0][0-9]{3}[A-Z]{3})".r
    val `9999A` = "([0-9]{4}[A-Z])".r
    val `9999AA` = "([0-9]{4}[A-Z]{2})".r
    val `A9AAA` = "([A-Z][0-9][A-Z]{3})".r
    val `A99AAA` = "([A-Z][0-9]{2}[A-Z]{3})".r
    val `A999AAA` = "([A-Z][0-9]{3}[A-Z]{3})".r

    vrm.toUpperCase.replace(OneSpaceCharPadding, "") match {
      case AA99AAA(v) => v.substring(0, 4) + OneSpaceCharPadding + v.substring(4, 7)
      case A9(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 2) + FiveSpaceCharPadding
      case A99(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 3) + FourSpaceCharPadding
      case A999(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 4) + ThreeSpaceCharPadding
      case A9999(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 5) + TwoSpaceCharPadding
      case AA9(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 3) + FourSpaceCharPadding
      case AA99(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 4) + ThreeSpaceCharPadding
      case AA999(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 5) + TwoSpaceCharPadding
      case AA9999(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 6) + OneSpaceCharPadding
      case AAA9(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 4) + ThreeSpaceCharPadding
      case AAA99(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 5) + TwoSpaceCharPadding
      case AAA999(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 6) + OneSpaceCharPadding
      case AAA9999(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 7)
      case AAA9Y(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 5) + TwoSpaceCharPadding
      case AAA99Y(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 6) + OneSpaceCharPadding
      case AAA999Y(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 7)
      case `9A`(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 2) + FiveSpaceCharPadding
      case `0009  A`(v) => v.substring(3, 4) + OneSpaceCharPadding + v.substring(4, 5) + FiveSpaceCharPadding
      case `9AA`(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 3) + FourSpaceCharPadding
      case `0009 AA`(v) => v.substring(3, 4) + OneSpaceCharPadding + v.substring(4, 6) + FourSpaceCharPadding
      case `9AAA`(v) => v.substring(0, 1) + OneSpaceCharPadding + v.substring(1, 4) + ThreeSpaceCharPadding
      case `0009AAA`(v) => v.substring(3, 4) + OneSpaceCharPadding + v.substring(4, 7) + ThreeSpaceCharPadding
      case `99A`(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 3) + FourSpaceCharPadding
      case `0099  A`(v) => v.substring(2, 4) + OneSpaceCharPadding + v.substring(4, 5) + FourSpaceCharPadding
      case `99AA`(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 4) + ThreeSpaceCharPadding
      case `0099 AA`(v) => v.substring(2, 4) + OneSpaceCharPadding + v.substring(4, 6) + ThreeSpaceCharPadding
      case `99AAA`(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 5) + TwoSpaceCharPadding
      case `0099AAA`(v) => v.substring(2, 4) + OneSpaceCharPadding + v.substring(4, 7) + TwoSpaceCharPadding
      case `999A`(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 4) + ThreeSpaceCharPadding
      case `0999  A`(v) => v.substring(1, 4) + OneSpaceCharPadding + v.substring(4, 5) + ThreeSpaceCharPadding
      case `999AA`(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 5) + TwoSpaceCharPadding
      case `0999 AA`(v) => v.substring(1, 4) + OneSpaceCharPadding + v.substring(4, 6) + TwoSpaceCharPadding
      case `9999AAA`(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 6) + OneSpaceCharPadding
      case `0999AAA`(v) => v.substring(1, 4) + OneSpaceCharPadding + v.substring(4, 7) + OneSpaceCharPadding
      case `9999A`(v) => v.substring(0, 4) + OneSpaceCharPadding + v.substring(4, 5) + TwoSpaceCharPadding
      case `9999AA`(v) => v.substring(0, 4) + OneSpaceCharPadding + v.substring(4, 6) + OneSpaceCharPadding
      case `A9AAA`(v) => v.substring(0, 2) + OneSpaceCharPadding + v.substring(2, 5) + TwoSpaceCharPadding
      case `A99AAA`(v) => v.substring(0, 3) + OneSpaceCharPadding + v.substring(3, 6) + OneSpaceCharPadding
      case `A999AAA`(v) => v.substring(0, 4) + OneSpaceCharPadding + v.substring(4, 7)
      case _ => vrm
    }
  }
}