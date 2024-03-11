package com.scylladb.migrator

//import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
//import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
//import com.amazonaws.services.dynamodbv2.model.ListTablesRequest
//import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import java.net.URI

object Test {

//  /** Client of a target Alternator instance */
//  val targetAlternator: AmazonDynamoDB = AmazonDynamoDBClientBuilder
//    .standard()
//    .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", "eu-central-1"))
//    .build()

  def main(args: Array[String]): Unit = {
    scala.sys.process.Process("curl http://localhost:8001").run().exitValue()
    println()

//    val sourceDDb = AmazonDynamoDBClientBuilder
//      .standard()
//      .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8001", "eu-central-1"))
//      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy")))
//      .build()
//
//    println(sourceDDb.listTables())

    val source = DynamoDbClient.builder()
      .endpointOverride(new URI("http://localhost:8001"))
      .region(Region.EU_CENTRAL_1)
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy")))
      .build()
    println(source.listTables())

//    println(targetAlternator.listTables())
  }
}
