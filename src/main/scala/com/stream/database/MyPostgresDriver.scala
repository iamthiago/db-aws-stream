package com.stream.database

import com.github.tminglei.slickpg._
import slick.jdbc.PostgresProfile

trait MyPostgresDriver extends PostgresProfile {
  override val api = MyAPI
  object MyAPI extends API
}

object MyPostgresDriver extends MyPostgresDriver
