package uk.gov.dvla.vehicles.presentation.common.services

import java.text.SimpleDateFormat
import java.util.Date

import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm

/**
 * Created by gerasimosarvanitis on 30/12/2014.
 */
object FeedbackMessageBuilder {

  import SEND.Contents


  def buildWith(form: FeedbackForm): Contents = {

    val date = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date())


    val sender =
      s"""received from: ${form.name.getOrElse("'no name given'")}
         | with email: ${form.email.getOrElse("'no email given'")}""".stripMargin

    val htmlContents = form.feedback.map {
      case ch if ch == '\n' => "<br />"
      case ch => ch
    }.mkString

    Contents(
      buildHtml(htmlContents, date, sender),
      buildText(form.feedback, date, sender)
    )
  }

  private def buildHtml(contents: String, date:String, sender: String): String =
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
        |<p>New feedback received on $date
        |<br />
        |from : $sender
        |</p>
        |
        |<p> $contents </p>
        |
        |<p></p>
        |</body>
        |</html>
      """.stripMargin

  private def buildText(contents: String, date: String, sender: String): String =
    s"""
        |
        |New feedback received on $date
        | from : $sender
        |
        |$contents
        |
      """.stripMargin
}
