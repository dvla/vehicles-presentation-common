package uk.gov.dvla.vehicles.presentation.common.queue

import play.api.libs.json.{Reads, Writes, Format}

import scala.concurrent.Future

sealed trait Priority
object Priority {
  case object Normal extends Priority
  case object Low extends Priority
}

sealed trait MessageAck
object MessageAck {
  case object Ack extends MessageAck
  case object Nack extends MessageAck
}

class QueueException extends Exception

//trait Message[T] {
//  def ack()
//
//  def rollback()
//
//  def get: T
//}

/**
 * A trait to hold just the close() method which should free any resources taken.
 */
trait ClosableChannel {
  def close()
}

/**
 * The out channel is used to send infinite number of generic messages to a queue.
 * It needs to be closed when the client no longer wants to send new messages.
 * @tparam T
 */
trait OutChannel[T] extends ClosableChannel {
  /**
   * Enqueues a message potentially with a given priority.
   * If this method finishes without exceptions the message has surely ended up in the queue.
   * If for some any the message cannot be put on the queue QueueException is thrown.
   * The messages are put on the queue either with Normal priority or with Low priority. Messages with
   * Normal priority are put in the queue in from of all the messages with Low priority.
   * @param message The message object will be serialised to JSON so appropriate JSON format should be in scope.
   * @param priority
   * @throws uk.gov.dvla.vehicles.presentation.common.queue.QueueException
   * @return Unit
   */
  @throws(classOf[QueueException])
  def put(message: T, priority: Priority)(implicit jsonWrites: Writes[T]): Unit
}

/**
 * The InChan is used to read messages from a queue in a streaming fashion.
 * @tparam T
 */
trait InChannel[T] extends ClosableChannel {

  /**
   * Register a method to be called when a message arrives. The method will be called for once for each message.
   * The messages will be send until close() is called. The messages will be arriving in priority order.
   * 
   * The on next method returns a Future[MessageAck]. If that future completes with Ack, the message will be
   * acknowledged to the queue. If the future completes with Rollback the message will be rolled back to the queue.
   * If the Future fails or times out the message will be rolled back to the queue.
   * @param onNext
   */
  def subscribe(onNext: T => Future[MessageAck])(implicit jsonReads: Reads[T])
}

trait Channel extends OutChannel with InChannel

trait ChannelFactory {
  def outChannel(queue: String): OutChannel

  def inChannel(queue: String): InChannel

  def channel(queue: String): Channel
}
