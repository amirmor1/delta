/*
 * Copyright (2021) The Delta Lake Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.delta.files

import org.apache.spark.sql.delta.{DeltaErrors, DeltaLog, Snapshot}
import org.apache.spark.sql.delta.actions.{AddFile, RemoveFile}
import org.apache.spark.sql.delta.actions.SingleAction.addFileEncoder
import org.apache.spark.sql.delta.commands.cdc.CDCReader
import org.apache.spark.sql.delta.commands.cdc.CDCReader._
import org.apache.hadoop.fs.Path

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.types.StructType

/**
 * A [[TahoeFileIndex]] for scanning a sequence of removed files as CDC. Similar to
 * [[TahoeBatchFileIndex]], the equivalent for reading [[AddFile]] actions.
 */
class TahoeRemoveFileIndex(
    spark: SparkSession,
    filesByVersion: Seq[CDCDataSpec[RemoveFile]],
    deltaLog: DeltaLog,
    path: Path,
    snapshot: Snapshot)
  extends TahoeCDCBaseFileIndex(spark, filesByVersion, deltaLog, path, snapshot) {


  override def additionalPartitionValues(): Map[String, String] =
    Map(CDC_TYPE_COLUMN_NAME -> CDC_TYPE_DELETE)

  override def partitionSchema: StructType =
    CDCReader.cdcReadSchema(snapshot.metadata.partitionSchema)

}
