package uk.gov.dvla.vehicles.presentation.common.queue

import java.io.IOException
import play.api.libs.json.{Reads, Writes}
import scala.concurrent.Future
import scala.util.Try

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

class QueueException(cause: Throwable) extends Exception(cause)

/**
 * A trait to hold just the close() method which should free any resources taken.
 */
trait ClosableChannel {
  @throws[IOException]
  def close()
}

/**
 * The out channel is used to send infinite number of generic messages.en to a queue.
 * It needs to be closed when the client no longer wants to send new messages.en.
 * @tparam T The type of message being published to the queue.
 */
trait OutChannel[T] extends ClosableChannel {
  /**
   * Enqueues a message potentially with a given priority.
   * If this method finishes without exceptions the message has surely ended up in the queue.
   * If for any reason the message cannot be put on the queue QueueException is thrown.
   * The messages.en are put on the queue either with Normal priority or with Low priority. Messages with
   * Normal priority are put in the queue in from of all the messages.en with Low priority.
   * @param message The message object will be serialised to JSON so appropriate JSON format should be in scope.
   * @param priority The message priority. During working hours messages.en are published with Normal priority.
   *                 Ot of hous they are published with Low priority.
   * @throws uk.gov.dvla.vehicles.presentation.common.queue.QueueException Thrown if there are any problems when
   *                                                                       publishing a message
   * @return Unit
   */
  @throws(classOf[QueueException])
  def put(message: T, priority: Priority = Priority.Normal)(implicit jsonWrites: Writes[T]): Unit
}

trait ChannelFactory {
  def outChannel[T](queue: String): Try[OutChannel[T]]

  /**
   * Register a method to be called when a message arrives. The method will be called once for each message.
   * The messages.en will be sent until close() is called. The messages.en will be arriving in priority order. If there
   * are multiple subscribers only one of them will receive and process a single message.
   *
   * The onNext method returns a Future[MessageAck]. If that future completes with Ack, the message will be
   * acknowledged to the queue. If the future completes with Rollback the message will be rolled back to the queue.
   * If the Future fails or times out the message will be rolled back to the queue.
   * @param queue The queue to subscribe to
   * @param onNext The callback to invoke to consume a published message
   */
  def subscribe[T](queue: String, onNext: T => Future[MessageAck])(implicit jsonReads: Reads[T]): Try[ClosableChannel]
}
