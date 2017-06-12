package com.stream.service

import java.util.UUID

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.event.LoggingReceive
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sns.scaladsl.SnsPublisher
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}
import com.stream.database.{Address, AddressRepository}
import com.stream.service.StreamActor.{Begin, Started}

import scala.concurrent.ExecutionContextExecutor

/**
  *
  * For every HTTP request received, we create one of this actor.
  * It is responsible to streaming objects from the database
  * and send it across an AWS topic.
  *
  * The HTTP sends a message with a topic name, which is necessary
  * to start out the actor. Once the message [[Begin]] arrives the actor,
  * it immediately replies to the sender with a [[Started]] message,
  * informing the streaming has been started on that particular topic.
  *
  * When the streaming is complete, this actor sends a [[PoisonPill]]
  * message to self which will be processed like any other message
  * on it's mailbox, and when processed, it stops the actor.
  *
  * @param requestId The unique identifier passed across all actions
  * @param topic The destination topic where the data should be sent
  */
class StreamActor(requestId: UUID, topic: String) extends Actor with ActorLogging {

  val credentials = new BasicAWSCredentials("x", "x")
  implicit val snsClient: AmazonSNSAsync =
    AmazonSNSAsyncClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .build()

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val addressToString: Flow[Address, String, NotUsed] = Flow[Address].map(_.toString)

  def receive = LoggingReceive {
    case Begin =>
      val replyTo = sender()

      replyTo ! Started(topic)

      log.info(s"RequestId: $requestId - Streaming has been started")

      Source
        .fromPublisher(AddressRepository.stream())
        .via(addressToString)
        .via(SnsPublisher.flow(topic))
        .toMat(Sink.ignore)(Keep.right)
        .run()
        .map { _ =>
          log.info(s"RequestId: $requestId - Streaming is done! Stopping this actor.")
          PoisonPill
        }
        .pipeTo(self)
  }
}

object StreamActor {
  def name(requestId: UUID) = s"stream-actor-${requestId.toString}"
  def props(requestId: UUID, topic: String) = Props(new StreamActor(requestId, topic))

  case object Begin
  case class Started(topic: String)
}
