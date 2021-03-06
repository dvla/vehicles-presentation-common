package uk.gov.dvla.vehicles.presentation.common.services

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailServiceSendRequest
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStats
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStatsFailure
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.healthstats.HealthStatsSuccess

/**
 * A simple service to send an email by making a rest call to the EmailService.
 *
 * Usage:
 *  val from = EmailAddress("dvla@co.uk", "DVLA Department of..")
 *  implicit configuration = SEND.EmailConfiguration("host", 25, "username", "passwd", from, None)
 *
 *  SEND email 'message withSubject 'subject to 'peopleList cc 'ccList send
 */
object SEND extends DVLALogger {

  import scala.language.{implicitConversions, postfixOps, reflectiveCalls}

  case class EmailConfiguration(from: From, feedbackEmail: From, whiteList: Option[List[String]])

  case class Contents(htmlMessage: String, plainMessage: String)

  case class Email(message: Contents, subject: String,
                   toPeople: Option[List[String]] = None,
                   ccPeople: Option[List[String]] = None) {

    def to(people: String*): Email = to(people.toList)

    def to(people: List[String]): Email = toPeople match {
      case None => this.copy(toPeople = Some(people.toList))
      case Some(_) => this.copy(toPeople = toPeople.map(_ ++ people.toList))
    }

    def cc(people: String*): Email = cc(people.toList)

    def cc(people: List[String]): Email = toPeople match {
      case None => this.copy(ccPeople = Some(people.toList))
      case Some(_) => this.copy(ccPeople = ccPeople.map(_ ++ people.toList))
    }
  }

  /** Generic trait to represent the Email Service */
  sealed trait EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration,
                                     emailService: EmailService,
                                     dateService: DateService,
                                     healthStats: HealthStats): Unit
  }

  /** A dummy email service, to handle non-white listed emails. */
  case class NonWhiteListedEmailOps(email: Email) extends EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration,
                                     emailService: EmailService,
                                     dateService: DateService,
                                     healthStats: HealthStats) = {
      val message =
        s"""send non-whitelisted : Got email with subject: ${email.subject}
           |${email.message}
           |to be sent to ${email.toPeople.mkString(" ")}
           |with cc ${email.ccPeople.mkString(" ")}
           |from ${config.from.email}
           |white list ${config.whiteList} so not sending"""
          .stripMargin
      logMessage(trackingId, Info, message)
    }
  }
  /** A no-ops email service that denotes an error in the email */
  object NoEmailOps extends EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration,
                                     emailService: EmailService,
                                     dateService: DateService,
                                     healthStats: HealthStats) =
      logMessage(trackingId, Info, "The email is incomplete. You cannot send that")
  }

  /** An smtp email service. Currently implemented by making a rest call to the email micro service */
  case class MicroServiceEmailOps(email: Email) extends EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration,
                                     emailService: EmailService,
                                     dateService: DateService,
                                     healthStats: HealthStats) = {
      import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailServiceImpl.ServiceName

      val from = From(config.from.email, config.from.name)

      val emailRequest: EmailServiceSendRequest = EmailServiceSendRequest(
        plainTextMessage = email.message.plainMessage,
        htmlMessage = email.message.htmlMessage,
        attachment = None,
        from = from,
        subject = email.subject,
        toReceivers = email.toPeople,
        ccReceivers = email.ccPeople
      )

      emailService.invoke(emailRequest, trackingId).onComplete {
        case Success(resp) =>
          logMessage(trackingId, Info, "Received success response back from email micro service")
          healthStats.success(HealthStatsSuccess(ServiceName, dateService.now))
        case Failure(fail) =>
          val msg = s"Failed to send email for ${email.toPeople.mkString(" ")}, reason was ${fail.getMessage}"
          logMessage(trackingId, Error, msg)
          healthStats.failure(HealthStatsFailure(ServiceName, dateService.now, fail))
      }
    }
  }

  /**
   * Method that will return the correct service implementation depending on who the email is being sent to.
   * @param mail the email to send
   * @param configuration the configuration needed
   * @return an appropriate instance of an email operations object
   */
  implicit def mailtoOps (mail: Email)(implicit configuration: EmailConfiguration): EmailOps = mail match {
    case Email(message, _, Some(toPeople), _) if !isWhiteListed(toPeople) => NonWhiteListedEmailOps(mail)
    case Email(message, _, Some(toPeople), _)                             => MicroServiceEmailOps(mail)
    case _                                                                => NoEmailOps
  }

  /**
   * Method that decides if the email has a white listed address. In case there is a white listed address then
   * the method will return true.  An empty white list config implies all addresses are white listed.
   * Note this method is package private so the test class can access it
   */
  private[services] def isWhiteListed(addresses: List[String])(implicit configuration: EmailConfiguration): Boolean =
    configuration.whiteList match {
      case Some(wl) => (for {
        address <- addresses
        whiteList <- Option("@test.com" :: wl) // Prepend @test.com to the white list
      } yield whiteList
          // Filter in all addresses whose domains match any of the domains in the white list
          .filter((domain: String) => address.endsWith(domain))).flatten match {
        case List() => false // Empty list means address not white listed
        case _ => true // Non-empty list means there is a white listed address
      }
      case _ => true // The white list is not defined (it is None) so let everything through
    }

  /**
   * Main entry point for the send email.
   * @param message the contents of the email.
   * @return an object that provides a withSubject method that will return an instance of the email message.
   */
  def email(message: Contents) = new { def withSubject(subject: String) = Email(message, subject) }
}
