package com.stream.directives

import scala.util.Try

import java.util.UUID
import akka.http.scaladsl.server.Directives._

trait RequestIdDirective {
  def extractRequestId = optionalHeaderValueByName("X-Request-Id").map {
    case Some(id) => Try(UUID.fromString(id)).getOrElse(UUID.randomUUID())
    case None => UUID.randomUUID()
  }
}
