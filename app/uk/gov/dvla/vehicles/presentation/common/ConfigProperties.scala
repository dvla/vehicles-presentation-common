package uk.gov.dvla.vehicles.presentation.common

import play.api.{Logger, Play}

object ConfigProperties {
  implicit val stringProp = (property: String) => Play.current.configuration.getString(property)
  implicit val intProp = (property: String) => Play.current.configuration.getInt(property)
  implicit val booleanProp = (property: String) => Play.current.configuration.getBoolean(property)
  implicit val longProp = (property: String) => Play.current.configuration.getLong(property)
  implicit val listStringProp = (property: String) => Play.current.configuration.getStringList(property)

  /**
   * Returns a property or throws a Runtime error if this property doesn't exist.
   * As an improvement we could wrap this into a Try.
   * Runtime Exception should be thrown for all mandatory properties.
   */
  def getProperty[T](property: String)(implicit propertyGetter: String => Option[T]): T =
    getOptionalProperty[T](property).getOrElse(error(property))

  def getDurationProperty(property: String): Long =
    Play.current.configuration.getMilliseconds(property).getOrElse(error(property))

  private def error(property: String) = {
    Logger.error(s"Property with name $property was not found. Try adding this property to application.conf file") // TODO not sure we need this line
    throw new RuntimeException(s"Property with name $property was not found. Try adding this property to application.conf file")
  }

  /**
   * Returns an optional property.
   */
  def getOptionalProperty[T](property: String)(implicit propertyGetter: String => Option[T]): Option[T] =
     propertyGetter(property)

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
