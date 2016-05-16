package uk.gov.dvla.vehicles.presentation.common.services

import java.text.SimpleDateFormat
import java.util.Date
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm

object FeedbackMessageBuilder {

  import SEND.Contents

  def buildWith(form: FeedbackForm, trackingId: TrackingId): Contents = {
    val date = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date())

    val sender =
      s"""received from: ${form.name.getOrElse("'no name given'")}
         |with email: ${form.email.getOrElse("'no email given'")}""".stripMargin

    def htmlSubstitution(ch: Char): String = ch match {
      case '\n' => "<br />"
      case '<' => "&lt;"
      case '>' => "&gt;"
      case '"' => "&quot;"
      case '\'' => "&#x27;"
      case '&' => "&amp;"
      case _ => ch.toString
    }

    val htmlContents = form.feedback.map { htmlSubstitution }.mkString

    Contents(
      buildHtml(htmlContents, date, sender, trackingId),
      buildText(form.feedback, date, sender, trackingId)
    )
  }

  private def buildHtml(contents: String, date: String,
                        sender: String, trackingId: TrackingId): String =
    s"""
        |<html>
        |<head>
        |</head>
        |<style>
        |p {
        |  line-height: 200%;
        |}
        |</style>
        |</head>
        |<body>
        |
        |<p>New feedback received on $date</p>
        |<p>$sender</p>
        |
        |<h2>Feedback</h2>
        |<p>$contents</p>
        |
        |<p>trackingId : $trackingId</p>
        |</body>
        |</html>""".stripMargin

  private def buildText(contents: String, date: String,
                        sender: String, trackingId: TrackingId): String =
    s"""
        |New feedback received on $date
        |$sender
        |
        |Feedback:
        |$contents
        |
        |trackingId : $trackingId""".stripMargin
}
