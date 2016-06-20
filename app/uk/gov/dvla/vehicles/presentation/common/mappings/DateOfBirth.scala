package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.Forms.{of, optional}
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import Date.notBefore
import Date.notInTheFuture
import Date.formatter
import Date.required

object DateOfBirth {

  final val ValidYearsAgo = 110

  private def genericDateOfBirth(implicit dateService: DateService) =
    of[LocalDate](formatter("error.dateOfBirth.invalid"))
      .verifying(notInTheFuture(Messages("error.dateOfBirth.inTheFuture")))
      .verifying(notBefore(dateService.now.toDateTime.toLocalDate.minusYears(ValidYearsAgo),
        Messages("error.dateOfBirth.110yearsInThePast")))

  def dateOfBirth()(implicit dateService: DateService) = genericDateOfBirth verifying required

  def optionalDateOfBirth()(implicit dateService: DateService) = optional(genericDateOfBirth)

}
