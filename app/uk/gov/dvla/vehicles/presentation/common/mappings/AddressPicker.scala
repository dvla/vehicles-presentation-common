package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.FormError
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.dvla.vehicles.presentation.common.model.Address

object AddressPicker {
  private final val AddressLinesFormat = """^[a-zA-Z0-9][A-Za-z0-9\s\-\,\.\/\\]*$""".r.pattern
  private final val PostTownFormat = """^[a-zA-Z][A-Za-z\s\-\,\.\/\\]*$""".r.pattern
  private final val MaxLengthOfLinesConcatenated = 120
  final val AddressLine1Id = "address-line-1"
  final val AddressLine2Id = "address-line-2"
  final val AddressLine3Id = "address-line-3"
  final val PostTownId = "post-town"
  final val CountyId = "county"
  final val PostcodeId = "post-code"

  def formatter() = new Formatter[Address] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Address] = {
      val filterEmpty = data.filterNot{case (_, v) => v.isEmpty}
      val line1 = filterEmpty.get(s"$key.$AddressLine1Id")
      val line2 = filterEmpty.get(s"$key.$AddressLine2Id")
      val line3 = filterEmpty.get(s"$key.$AddressLine3Id")
      val postTown = filterEmpty.get(s"$key.$PostTownId")
      val county = filterEmpty.get(s"$key.$CountyId")
      val postCode = filterEmpty.get(s"$key.$PostcodeId")

      type SFE = Seq[FormError]

      val errors = line1.fold[SFE](Seq(FormError(s"$key.$AddressLine1Id", "error.address.addressLine1"))) { line =>
        val addressLineLengthErr =
          if (Seq(line1, line2, line3).flatten.mkString(",").length > MaxLengthOfLinesConcatenated)
            Seq(FormError(key, "error.address.postCode"))
          else Nil

        Seq(AddressLine1Id, AddressLine2Id, AddressLine3Id).map { lkey =>
          filterEmpty.get(s"$key.$lkey").fold[Option[FormError]](None) { line =>
            if (AddressLinesFormat.matcher(line).matches()) None
            else Some(FormError(s"$key.$lkey", "error.address.characterInvalid"))
          }
        }.flatten ++ addressLineLengthErr
      } ++ postTown.fold[SFE](Seq(FormError(s"$key.$PostTownId", "error.address.postTown"))) { postTown =>
        if (PostTownFormat.matcher(postTown).matches()) Nil
        else Seq(FormError(s"$key.$PostTownId", "error.postTown.characterInvalid"))
      } ++ postCode.fold[SFE](Seq(FormError(s"$key.$PostcodeId", "error.address.postCode"))) { postCode =>
        Postcode.postcode.withPrefix(s"$key.$PostcodeId").bind(Map(s"$key.$PostcodeId" -> postCode)) match {
          case Left(errors) => errors
          case Right(result) => Nil
        }
      }

      if (!errors.isEmpty) Left(errors)
      else Right(Address(
        line1.get,
        line2,
        line3,
        postTown.get,
        county,
        postCode.get.toUpperCase()
      ))
    }

    override def unbind(key: String, value: Address): Map[String, String] = Map(
      s"$key.$AddressLine1Id" -> value.streetAddress1,
      s"$key.$PostTownId" -> value.postTown,
      s"$key.$PostcodeId" -> value.postCode
    ) ++
      toMap(value.streetAddress2, s"$key.$AddressLine2Id") ++
      toMap(value.streetAddress3, s"$key.$AddressLine3Id") ++
      toMap(value.county, s"$key.$CountyId")


    private def toMap(opt: Option[String], key: String) = opt.fold(Map[String, String]())(value => Map(key -> value))
  }

  val mapAddress = of[Address](AddressPicker.formatter())
}
