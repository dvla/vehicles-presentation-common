package uk.gov.dvla.vehicles.presentation.common.services

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by gerasimosarvanitis on 30/12/2014.
 */
object FeedbackMessageBuilder {

  import SEND.Contents


  def buildWith(contents: String): Contents = {

    val date = new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date())

    val htmlContents = contents.map {
      case ch if ch == '\n' => "<br />"
      case ch => ch
    }.mkString

    Contents(
      buildHtml(htmlContents, date),
      buildText(contents, date)
    )
  }

  private def buildHtml(contents: String, date:String): String =
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
        |<p> $contents </p>
        |
        |<p></p>
        |</body>
        |</html>
      """.stripMargin

  private def buildText(contents: String, date: String): String =
    s"""
        |
        |New feedback received on $date
        |
        |$contents
        |
      """.stripMargin
}
