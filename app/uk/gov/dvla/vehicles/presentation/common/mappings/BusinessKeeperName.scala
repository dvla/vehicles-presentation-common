package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views
import views.helpers.FormExtensions.nonEmptyTextWithTransform
import views.constraints.BusinessKeeperName.{validBusinessKeeperName, atLeastACharacter}

object BusinessKeeperName {
  final val MinLength = 2
  final val MaxLength = 30

  def businessKeeperNameMapping: Mapping[String] =
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validBusinessKeeperName verifying atLeastACharacter
}
