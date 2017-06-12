package com.stream

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.stream.directives.RequestIdDirective
import com.stream.service.StreamActor
import com.stream.service.StreamActor._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Boot extends App with RequestIdDirective {

  val log = LoggerFactory.getLogger(this.getClass)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(1.second)

  val route: Route = path("stream" / Segment) { topic =>
    get {
      extractRequestId { requestId =>
        val streamActor = system.actorOf(StreamActor.props(requestId, topic), StreamActor.name(requestId))
        onComplete((streamActor ? Begin).mapTo[Started]) {
          case Success(result) => complete(Accepted, s"The stream has been successfully initiate to topic: ${result.topic}")
          case Failure(e) => complete(InternalServerError, s"Something went wrong: $e")
        }
      }
    }
  }

  Http().bindAndHandle(route, "localhost", 8080).onComplete {
    case Success(_) => log.info("Application Started")
    case Failure(e) => log.error("Could not start the server", e)
  }
}
