package com.stream.database

import com.stream.database.MyPostgresDriver.api._
import slick.basic.DatabasePublisher

case class Address(state: Option[String], city: Option[String], neighborhood: Option[String], street: Option[String], streetNumber: Option[String], zipCode: Option[String])

class AddressModel(tag: Tag) extends Table[Address](tag, "address") {

  def state = column[Option[String]]("state")
  def city = column[Option[String]]("city")
  def neighborhood = column[Option[String]]("neighborhood")
  def street = column[Option[String]]("street")
  def streetNumber = column[Option[String]]("street_number")
  def zipCode = column[Option[String]]("zip_code")

  def * = (state, city, neighborhood, street, streetNumber, zipCode) <> (Address.tupled, Address.unapply)
}

class AddressRepository extends PostgresConnection {

  val table: TableQuery[AddressModel] = TableQuery[AddressModel]

  def stream(): DatabasePublisher[Address] = stream {
    val query = for {
      t <- table
    } yield t

    query.result
  }
}

object AddressRepository extends AddressRepository