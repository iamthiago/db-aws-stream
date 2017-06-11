# From Postgres to AWS SNS

This is a simple project showing how to stream things from Postgres using Slick and publishing it to a SNS Topic using Alpakka.

## Motivation

I often see people around with streaming requirements in their projects. This is an attempt to show how it works on the scala world and how it could be at the same time, simple and powerful.

## Flow

1. Stream data directly from postgres using Slick and it's DatabasePublisher return type.

2. Parse it address to a string format, which could be sent to SNS. In this simple example I'm only calling toString, but in a real world application, you can consider a json format to be sent.

3. Publish the addresses to a SNS topic in the Sink process.

## Useful links

* http://slick.lightbend.com/doc/3.2.0/
* http://slick.lightbend.com/doc/3.2.0/dbio.html#streaming
* http://developer.lightbend.com/docs/alpakka/current/sns.html
* http://developer.lightbend.com/docs/alpakka/current/sns.html#using-a-sink

## TO-DO

* Docker
* CI
* Tests