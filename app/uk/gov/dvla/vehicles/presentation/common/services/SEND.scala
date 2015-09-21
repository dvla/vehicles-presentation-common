package uk.gov.dvla.vehicles.presentation.common.services

import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.{EmailService, EmailServiceSendRequest}

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
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration, emailService: EmailService): Unit
  }

  /** A dummy email service, to handle non-white listed emails. */
  case class NonWhiteListEmailOps(email: Email) extends EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration, emailService: EmailService) = {
      val message =
        s"""Got email with subject: ${email.subject}
           |${email.message}
           |to be sent to ${email.toPeople.mkString(" ")}
           |with cc ${email.ccPeople.mkString(" ")}
           |from ${config.from.email}
           |Receiver was not in the white list so not sending""".stripMargin
      logMessage(trackingId, Info, message)
    }
  }
  /** A no-ops email service that denotes an error in the email */
  object NoEmailOps extends EmailOps {
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration, emailService: EmailService) =
      logMessage(trackingId, Info, "The email is incomplete. You cannot send that")
  }

  /** An smtp email service. Currently implemented by making a rest call to the email micro service */
  case class MicroServiceEmailOps(email: Email) extends EmailOps{
    def send(trackingId: TrackingId)(implicit config: EmailConfiguration, emailService: EmailService) = {
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

      emailService.invoke(emailRequest, trackingId).onFailure {
        case fail =>
          val msg = s"Failed to send email for ${email.toPeople.mkString(" ")}, reason was ${fail.getMessage}"
          logMessage(trackingId, Error, msg)
      }
    }
  }

  /**
   * Validation method that will return the correct service implementation depending on the email.
   * @param mail the email to send
   * @param configuration the configuration needed
   * @return an appropriate instance of an email operations object
   */
  implicit def mailtoOps (mail: Email)(implicit configuration: EmailConfiguration): EmailOps = mail match {
    case Email(message, _, Some(toPeople), _) if !isWhiteListed(toPeople) => NonWhiteListEmailOps(mail)
    case Email(message, _, Some(toPeople), _)                             => MicroServiceEmailOps(mail)
    case _                                                                => NoEmailOps
  }

  /**
   * Method that decides if the email has a white listed address. In case there is a white listed address then
   * the method will return true.  An empty white list config implies all addresses are white listed.
   */
  def isWhiteListed(addresses: List[String])(implicit configuration: EmailConfiguration): Boolean = {

    configuration.whiteList match {
      case Some(lst) => (for {
        address <- addresses
        whiteList <- Option("@test.com" :: lst)
      } yield whiteList
          .filter((domain: String) => address.endsWith(domain))).flatten match {
        case List() => false
        case _ => true
      }
      case _ => true
    }
  }

  /**
   * Main entry point for the send email.
   * @param message the contents of the email.
   * @return an object that provides a withSubject method that will return an instance of the email message.
   */
  def email(message: Contents) = new { def withSubject(subject: String) = Email(message, subject) }
}
