package mappings.common

import play.api.data.Mapping
import play.api.data.Forms._
import models.domain.common.AddressLinesModel
import utils.helpers.FormExtensions._

object AddressLines {
  final val AddressLinesId = "addressLines"
  final val BuildingNameOrNumberId = "buildingNameOrNumber"
  final val Line2Id = "line2"
  final val Line3Id = "line3"
  final val postTownId = "postTown"
  final val BuildingNameOrNumberMinLength = 4
  final val PostTownMinLength = 3
  final val LineMaxLength = 30
  final val MaxLengthOfLinesConcatenated = 120

  final val AddressLinesCacheKey = "addressLines"
  final val BuildingNameOrNumberHolder = "No building name/num supplied"

  final val BuildingNameOrNumberIndex = 0
  final val Line2Index = 1
  final val Line3Index = 2
  final val emptyLine = ""

  def addressLines: Mapping[AddressLinesModel] = mapping(
    BuildingNameOrNumberId -> nonEmptyTrimmedText(minLength = BuildingNameOrNumberMinLength, maxLength = LineMaxLength),
    Line2Id -> optional(trimmedText(maxLength = LineMaxLength)),
    Line3Id -> optional(trimmedText(maxLength = LineMaxLength)),
    postTownId -> nonEmptyTrimmedText(minLength = PostTownMinLength, maxLength = LineMaxLength)
  )(AddressLinesModel.apply)(AddressLinesModel.unapply)
}
