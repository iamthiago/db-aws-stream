package com.stream.database

import slick.dbio.Effect.Read
import slick.jdbc.PostgresProfile.api._

trait PostgresConnection {
  val db = Database.forConfig("my.db")
  val disableAutocommit = SimpleDBIO(_.connection.setAutoCommit(false))

  def run[T](dBIOAction: DBIOAction[T, NoStream, Nothing]) = db.run(dBIOAction)
  def stream[T](dBIOAction: DBIOAction[Seq[T], Streaming[T], Read]) = db.stream(disableAutocommit andThen dBIOAction.withStatementParameters(fetchSize = 1000))
}
