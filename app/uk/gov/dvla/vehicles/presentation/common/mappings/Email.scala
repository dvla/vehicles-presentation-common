package uk.gov.dvla.vehicles.presentation.common.mappings

import play.api.data.format.Formats.stringFormat
import play.api.data.Forms.of
import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Email.emailAddress

object Email {
  final val EmailMinLength = 3
  final val EmailMaxLength = 254
  final val EmailUsernameMaxLength = 64
  final val EmailDomainSectionMaxLength = 63
  final val InvalidUsernameChar = "\"."
  final val InvalidDomainStartEndChar = "-"
  final val InvalidDomainContentChar = "/"

  def email: Mapping[String] = of[String] verifying emailAddress
}