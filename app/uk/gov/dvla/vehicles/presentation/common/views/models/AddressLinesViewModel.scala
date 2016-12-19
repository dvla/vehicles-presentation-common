package uk.gov.dvla.vehicles.presentation.common.views.models

import play.api.data.Forms.optional
import play.api.data.Mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.views.helpers.FormExtensions.{nonEmptyTextWithTransform, textWithTransform}

case class AddressLinesViewModel(buildingNameOrNumber: String,
                                 line2: Option[String] = None,
                                 line3: Option[String] = None,
                                 postTown: String) {

  def toViewFormat: Seq[String] = Seq(
    Some(buildingNameOrNumber.toUpperCase),
    line2.map(_.toUpperCase),
    line3.map(_.toUpperCase),
    Some(postTown.toUpperCase)
  ).flatten

  def totalCharacters = toViewFormat.map(_.length).sum
}

object AddressLinesViewModel {
  implicit val JsonFormat = Json.format[AddressLinesViewModel]
  final val AddressLinesCacheKey = "addressLines"
  implicit val Key = CacheKey[AddressLinesViewModel](AddressLinesCacheKey)

  object Form {
    final val AddressLinesId = "addressLines"
    final val BuildingNameOrNumberId = "buildingNameOrNumber"
    final val Line2Id = "line2"
    final val Line3Id = "line3"
    final val PostTownId = "postTown"
    final val BuildingNameOrNumberMinLength = 4
    final val PostTownMinLength = 3
    final val TownMaxLength = 20 // NOTE: this must be kept at twenty despite web interface allowing thirty
    final val LineMaxLength = 30

    def mapping(postTownMaxLength: Int = TownMaxLength): Mapping[AddressLinesViewModel] = play.api.data.Forms.mapping(
      BuildingNameOrNumberId ->
        nonEmptyTextWithTransform(fieldTransform)(minLength = BuildingNameOrNumberMinLength, maxLength = LineMaxLength),
      Line2Id -> optional(textWithTransform(fieldTransform)(maxLength = LineMaxLength)),
      Line3Id -> optional(textWithTransform(fieldTransform)(maxLength = LineMaxLength)),
      PostTownId -> nonEmptyTextWithTransform(fieldTransform)(minLength = PostTownMinLength, maxLength = postTownMaxLength)
    )(AddressLinesViewModel.apply)(AddressLinesViewModel.unapply)

    private def fieldTransform(s: String) = s.trim.toUpperCase
  }
}
