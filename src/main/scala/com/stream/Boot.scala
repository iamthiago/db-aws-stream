package com.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sns.scaladsl._
import akka.stream.scaladsl._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}
import com.stream.database.{Address, AddressRepository}

object Boot extends App {

  val credentials = new BasicAWSCredentials("x", "x")
  implicit val snsClient: AmazonSNSAsync =
    AmazonSNSAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val addressToString = Flow[Address].map(_.toString) //TODO: make it a json string or similar

  Source
    .fromPublisher(AddressRepository.stream())
    .via(addressToString)
    .runWith(SnsPublisher.sink("my-sns-topic"))
}
