package uk.gov.dvla.vehicles.presentation.common.testhelpers

import play.api.Application
import play.api.i18n._

/**
 * optimizes the loading of the messages so that they are loaded only one per JVM
 * this is useful for unit & integration tests that start a new Fakeapplication for each test
 */
class CachedMessagesPlugin(app: Application) extends DefaultMessagesPlugin(app) {

  /**
   * The underlying internationalisation API.
   */
  override lazy val api = CachedMessagesPlugin.cache match {
    case Some(messagesApi) => messagesApi
    case None => throw new RuntimeException //should not get here
  }

  /**
   * Loads all configuration and message files defined in the classpath.
   * into a cache
   */
  override def onStart() = if(CachedMessagesPlugin.cache == None){ CachedMessagesPlugin.cache = Some(MessagesApi(messages))}
}

/**
 * holds the cached messages
 */
object CachedMessagesPlugin{
  var cache :Option[MessagesApi]= None
}
