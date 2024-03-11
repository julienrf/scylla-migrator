package com.scylladb.migrator

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}

object Test {

//  /** Client of a target Alternator instance */
//  val targetAlternator: AmazonDynamoDB = AmazonDynamoDBClientBuilder
//    .standard()
//    .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "eu-central-1"))
//    .build()

  def main(args: Array[String]): Unit = {
    scala.sys.process.Process("curl http://localhost:8001").run().exitValue()

    Thread.sleep(2000)
    val sourceDDb = AmazonDynamoDBClientBuilder
      .standard()
      .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8001", "eu-central-1"))
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy")))
      .build()
//    println(sourceDDb.listTables())


//    println(targetAlternator.listTables())
  }
}
