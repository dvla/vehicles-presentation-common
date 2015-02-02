package uk.gov.dvla.vehicles.presentation.common

import scala.reflect.runtime.universe.TypeTag
import play.api.{Logger, Play}

object ConfigProperties {

  /**
   * Returns a property or throws a Runtime error if this property doesn't exist.
   * As an improvement we could wrap this into a Try.
   * Runtime Exception should be thrown for all mandatory properties.
   */
  def getProperty[T:TypeTag](property: String): T =
    PropertyExtractor[T](property) match {
      case Some(s) => s
      case None => error(property)
    }

  def getDurationProperty(property: String): Long = {
    Play.current.configuration.getMilliseconds(property) match {
      case Some(propValue) => propValue
      case None => error(property)
    }
  }

  private def error(property: String) = {
    Logger.error(s"Property with name $property was not found. Try adding this property to application.conf file") // TODO not sure we need this line
    throw new RuntimeException(s"Property with name $property was not found. Try adding this property to application.conf file")
  }

  /**
   * Returns an optional property.
   */
  def getOptionalProperty[T: TypeTag](property: String): Option[T] =
    PropertyExtractor[T](property)


  /**
   * This is a helper object that will extract the property based on the correct type. It uses reflection to do that, so
   * type T should always be present.
   *
   * CODE:  The proper check should be:
   *        case x if x.tpe == typeOf[...]
   *        but this fails sometimes with no apparent reason. The solution was to force the check into the Strings. This
   *        is the reason we do this check:
   *        case x if x.tpe.toString == "..."
   */
  object PropertyExtractor {
    private val lock = new Object

    import scala.reflect.runtime.universe._

    def apply[T: TypeTag](property: String): Option[T] = lock.synchronized {
      typeTag[T] match {
        case x if x.tpe.toString == "String" => Play.current.configuration.getString(property).map(_.asInstanceOf[T])
        case x if x.tpe.toString == "Int" => Play.current.configuration.getInt(property).map(_.asInstanceOf[T])
        case x if x.tpe.toString == "Boolean" => Play.current.configuration.getBoolean(property).map(_.asInstanceOf[T])
        case x if x.tpe.toString == "Long" => Play.current.configuration.getLong(property).map(_.asInstanceOf[T])
        case x if x.tpe.toString == "java.util.List[String]" =>
          Play.current.configuration.getStringList(property).map(_.asInstanceOf[T])
        case _ => Logger.error(s"type ${typeOf[T]} requested for property $property is not supported by the application"); None
      }
    }
  }

  /**
   * helper method to map the java.util.List to a scala List.
   * By default all the lists in the properties are mapped to a java List.
   */
  def getStringListProperty(property: String): Option[List[String]] = {
    import collection.JavaConversions._ // configuration.getStringList returns a Java list but we need a scala list
    // so import this here and convert the list bellow to a scala list
    getOptionalProperty[java.util.List[String]](property).map(_.toList)
  }

}
