package uk.gov.dvla.vehicles.presentation.common.mappings

import org.joda.time.LocalDate
import play.api.data.Forms.{of, optional}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import Date.formatter
import Date.notInTheFuture

object OptionalDate {

    val optionalDateMapping = optional(of[LocalDate](formatter()))

    def optionalNonFutureDateMapping(implicit dateService: DateService) =
      optional(of[LocalDate](formatter()) verifying notInTheFuture())

}
