package uk.gov.dvla.vehicles.presentation.common.filters

import org.mockito.{ArgumentCaptor, Mockito}
import org.mockito.Mockito.{times, when}
import org.scalatest.mock.MockitoSugar
import org.slf4j.Logger
import play.api.LoggerLike

/**
  * Mock implementation of Play's play.api.LoggerLike trait
  * which provides methods that allow us to capture the strings that
  * are actually being logged so we can write tests against them
  */
class MockLogger extends LoggerLike with MockitoSugar {
  // Override the trait's logger and point it to a mock implementation
  override val logger = mock[Logger]

  when(logger.isTraceEnabled).thenReturn(true)
  when(logger.isDebugEnabled).thenReturn(true)
  when(logger.isInfoEnabled).thenReturn(true)
  when(logger.isWarnEnabled).thenReturn(true)
  when(logger.isErrorEnabled).thenReturn(true)

  /**
    * Captures the text that was sent to the info method of the logger
    * @return the String that was sent to the info method of the logger
    */
  def captureLogInfo(): String = {
    val captor = ArgumentCaptor.forClass(classOf[String])
    Mockito.verify(logger).info(captor.capture())
    captor.getValue
  }

  /**
    * Captures the strings that were sent to the info method of the logger
    * @param times1 how many times we expect the info method called on the logger
    * @return the collection of strings that were sent to the info method of the logger
    */
  def captureLogInfos(times1: Int): java.util.List[String] = {
    val captor = ArgumentCaptor.forClass(classOf[String])
    Mockito.verify(logger, times(times1)).info(captor.capture())
    captor.getAllValues
  }

  /**
    * Resets the mock logger if it needs to be reused
    */
  def reset(): Unit = Mockito.reset(logger)
}