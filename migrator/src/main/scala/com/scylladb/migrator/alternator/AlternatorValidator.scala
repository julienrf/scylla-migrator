package com.scylladb.migrator.alternator

import com.scylladb.migrator.config.{ MigratorConfig, SourceSettings, TargetSettings }
import com.scylladb.migrator.validation.RowComparisonFailure
import com.scylladb.migrator.readers
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.jdk.CollectionConverters._

object AlternatorValidator {

  /**
    * Checks that the target Alternator database contains the same data
    * as the source DynamoDB database.
    *
    * @return A list of comparison failures (which is empty if the data
    *         are the same in both databases).
    */
  def runValidation(
    sourceSettings: SourceSettings.DynamoDB,
    targetSettings: TargetSettings.DynamoDB,
    config: MigratorConfig)(implicit spark: SparkSession): List[RowComparisonFailure] = {

    val (source, sourceTableDesc) = readers.DynamoDB.readRDD(spark, sourceSettings)
    val sourceTableKeys = sourceTableDesc.getKeySchema.asScala.toList

    val sourceByKey: RDD[(List[DdbValue], collection.Map[String, DdbValue])] =
      source
        .map {
          case (_, item) =>
            val key = sourceTableKeys
              .map(keySchemaElement =>
                DdbValue.from(item.getItem.get(keySchemaElement.getAttributeName)))
            (key, item.getItem.asScala.map { case (k, v) => (k, DdbValue.from(v)) })
        }

    val (target, _) = readers.DynamoDB.readRDD(
      spark,
      targetSettings.endpoint,
      targetSettings.finalCredentials,
      targetSettings.region,
      targetSettings.table,
      targetSettings.scanSegments,
      targetSettings.maxMapTasks,
      readThroughput        = None,
      throughputReadPercent = None
    )

    // Define some aliases to prevent the Spark engine to try to serialize the whole object graph
    val renamedColumn = config.renamesMap
    val configValidation = config.validation

    val targetByKey: RDD[(List[DdbValue], collection.Map[String, DdbValue])] =
      target
        .map {
          case (_, item) =>
            val key = sourceTableKeys
              .map { keySchemaElement =>
                val columnName = keySchemaElement.getAttributeName
                DdbValue.from(item.getItem.get(renamedColumn(columnName)))
              }
            (key, item.getItem.asScala.map { case (k, v) => (k, DdbValue.from(v)) })
        }

    sourceByKey
      .leftOuterJoin(targetByKey)
      .flatMap {
        case (_, (l, r)) =>
          RowComparisonFailure.compareDynamoDBRows(
            l,
            r,
            renamedColumn,
            configValidation.floatingPointTolerance
          )
      }
      .take(config.validation.failuresToFetch)
      .toList
  }

}
