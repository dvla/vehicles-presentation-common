package uk.gov.dvla.vehicles.presentation.common.services

import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From
import webserviceclients.emailservice.{EmailService, EmailServiceSendRequest}

import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global


/**
 * A simple service to send an email, leveraging the apache commons email library.
 *
 * Usage:
 *  val from = EmailAddress("dvla@co.uk", "DVLA Department of..")
 *  implicit configuration = SEND.EmailConfiguration("host", 25, "username", "passwd", from, None)
 *
 *  SEND email 'message withSubject 'subject to 'peopleList cc 'ccList send
 *
 * Created by gerasimosarvanitis on 03/12/2014.
 */
object SEND {

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
    def send(trackingId: String)(implicit config: EmailConfiguration, emailService: EmailService): Unit
  }

  /** A dummy email service, to send the white listed emails. */
  case class NonWhiteListEmailOps(email: Email) extends EmailOps {
    def send(trackingId: String)(implicit config: EmailConfiguration, emailService: EmailService) = {
      val message = s"""Got email with contents: (${email.subject} - ${email.message} ) to be sent to ${email.toPeople.mkString(" ")}

          ||with cc (${email.ccPeople.mkString(" ")}
)
         |${config.from.email}. Receiver was in whitelist""".stripMargin

      Logger.info(

        message)
    }
  }
  /** A no-ops email service that denotes an error in the email */
  object NoEmailOps extends EmailOps {
    def send(trackingId: String)(implicit config: EmailConfiguration, emailService: EmailService) =
      Logger.info("The email is incomplete. you cannot send that")

  }

  /** An smtp email service. Currently implemented using the Apache commons email library */
  case class SmtpEmailOps(email: Email) extends EmailOps{

    def send(trackingId: String)(implicit config: EmailConfiguration, emailService: EmailService) = {

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
        case fail => Logger.error(
          s"""Failed to send email for ${email.toPeople.
            mkString(" ")}
             |reason was ${fail.getMessage}""".stripMargin)
      }
    }
  }

  /**
   * Validation method that will return the correct service implementation depending on the email.
   * @param mail the email to send
   * @param configuration the configuration needed
   * @returnan appropriate instance of an email.
   */
  implicit def mailtoOps (mail: Email)(implicit configuration: EmailConfiguration): EmailOps = mail match {
    case Email(message, _, Some(toPeople), _) if !isWhiteListed(toPeople) => NonWhiteListEmailOps(mail)
    case Email(message, _, Some(toPeople), _)                             => SmtpEmailOps(mail)
    case _                                                                => NoEmailOps
  }

  /**
   * private method to decide if the email has a white listed address. In case there is a white listed address then the
   * method will return true.  An empty white list config implies all addresses are white listed.
   */
  def isWhiteListed(addresses: List[String])(implicit configuration: EmailConfiguration): Boolean = {

    configuration.whiteList match {
      case Some(lst) => (for {
        address <- addresses
        whiteList <- Option("@test.com" :: lst)
      } yield whiteList.
          filter((domain: String) => address.endsWith(domain))).flatten match {
        case List() => false
        case _ => true
      }
      case _ => true
    }
  }

  /**
   * Main entry point for the send email.
   * @param message the contents of the email.
   * @return an instance if the email message.
   */
  def email(message: Contents) = new { def withSubject(subject: String) = Email(message, subject) }
}
