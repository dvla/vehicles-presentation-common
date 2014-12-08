package uk.gov.dvla.vehicles.presentation.common

import play.api.Play
import scala.util.{Success, Try}

object ConfigProperties {

  def getProperty(property: String, default: Int) =
    Try(Play.current.configuration.getInt(property).getOrElse(default)) match {
      case Success(s) => s
      case _ => default
    }

  def getProperty(property: String, default: String) =
    Try(Play.current.configuration.getString(property).getOrElse(default)) match {
      case Success(s) => s
      case _ => default
    }

  def getProperty(property: String, default: Boolean) =
    Try(Play.current.configuration.getBoolean(property).getOrElse(default)) match {
      case Success(s) => s
      case _ => default
    }

  def getProperty(property: String, default: Long) =
    Try(Play.current.configuration.getLong(property).getOrElse(default)) match {
      case Success(s) => s
      case _ => default
    }
  def getStringListProperty(property: String): Option[List[String]] = {
    import collection.JavaConversions._ // configuration.getStringList returns a Java list but we need a scala list
                                        // so import this here and convert the list bellow to a scala list
    Try(Play.current.configuration.getStringList(property).map(_.toList)) match {
      case Success(s) => s
      case _ => None
    }
  }

  def getDurationProperty(property: String, default: Long) =
    Try(Play.current.configuration.getMilliseconds(property).getOrElse(default)) match {
      case Success(s) => s
      case _ => default
    }
}
