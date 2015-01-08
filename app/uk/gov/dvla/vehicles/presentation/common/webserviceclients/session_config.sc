import play.api.{Play, Logger}

import scala.reflect.runtime.universe.TypeTag

val string: Option[String] = Some("Hello")
val integer: Option[Int] = Some(12)
val nstring: Option[String] = None
val ninteger: Option[Int] = None


object PropertyExtractor {
  import scala.reflect.runtime.universe._
  def apply[T: TypeTag](property: String): Option[T] = typeOf[T] match {
    case x if x == typeOf[String] => nstring.map(_.asInstanceOf[T])
    case x if x == typeOf[Int] => integer.map(_.asInstanceOf[T])
  }
}

def getProperty[T:TypeTag](property: String): T = {
  PropertyExtractor[T](property) match {
    case Some(s) => s
    case None => {
      Logger.error(s"property with name $property was not found. try adding this property to application.conf file")
      throw new RuntimeException
    }
  }
}

def getOptionalProperty[T: TypeTag](property: String): Option[T] = {
  PropertyExtractor[T](property)
}

getProperty[Int]("hello")
getProperty[String]("hello")
getOptionalProperty[String]("hello")
