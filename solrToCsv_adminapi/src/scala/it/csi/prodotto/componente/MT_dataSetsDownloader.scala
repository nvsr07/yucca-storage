package it.csi.prodotto.componente

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable
import scala.collection.mutable.Buffer
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import org.apache.commons.csv.QuoteMode
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.FileUtil
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.permission.FsPermission
import org.apache.spark.SparkContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions.coalesce
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.date_format
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.to_utc_timestamp
import org.apache.spark.sql.types.FloatType
import org.apache.spark.sql.types.StringType
import org.csi.yucca.adminapi.client.BackOfficeCreateClient
import org.csi.yucca.adminapi.client.BackofficeListaClient
import org.csi.yucca.adminapi.response.AllineamentoScaricoDatasetResponse
import org.csi.yucca.adminapi.response.BackofficeDettaglioStreamDatasetResponse
import org.csi.yucca.adminapi.response.OrganizationResponse
import org.csi.yucca.helper.SolrDelegate
import org.slf4j.LoggerFactory

class MT_dataSetsDownloader(
  organization: OrganizationResponse,
  datasetCode: String,
  newObjectID: String,
  sparkContext: SparkContext,
  solrDelegate: SolrDelegate,
  sqlContext: SQLContext,
  adminApiUrl: String) extends Callable[Outcomes] {

  val LOG = LoggerFactory.getLogger(getClass)

  def getListStreamDataset(organizationCode: String): Option[List[BackofficeDettaglioStreamDatasetResponse]] = {
    try {
      val result = BackofficeListaClient.getListStreamDataset(adminApiUrl, organizationCode, "MultiThreadDownloadCSV")
      if (result != null)
        Some(result.asScala.toList)
      else
        Some(List.empty[BackofficeDettaglioStreamDatasetResponse])
    } catch {
      case t: Throwable => None
    }
  }

  def getAllineamentoByIdOrganization(idOrganization: Integer): Option[List[AllineamentoScaricoDatasetResponse]] = {
    try {
      val result = BackofficeListaClient.getAllineamentoByIdOrganization(adminApiUrl, idOrganization, "MultiThreadDownloadCSV")
      if (result != null)
        Some(result.asScala.toList)
      else
        Some(List.empty[AllineamentoScaricoDatasetResponse])
    } catch {
      case t: Throwable => None
    }
  }

  def checkError(executorService: ExecutorCompletionService[Outcomes], streamDatasets: Buffer[BackofficeDettaglioStreamDatasetResponse]): Unit = {

    LOG.info("MultitThreadDDownloadCSV.checkError ==> BEGIN")

    if (streamDatasets == null)
      return

    try {
      var count = 0

      for (
        streamDataset <- streamDatasets if datasetCode.isEmpty || datasetCode.equals(streamDataset.getDataset.getDatasetcode())
      ) {
        count += 1

        breakable {
          if (executorService == null || executorService.take == null || executorService.take.get == null) {
            LOG.info("executorService.take() E' NULLO")
            break
          }
          val resultDownload = executorService.take().get();
          if (resultDownload.hasErrors) {
            LOG.info("MultitThreadDDownloadCSV.checkError ==> 4")
            LOG.error("[[DownloadCSV::main]] ERROR on DS " + resultDownload.tenantCode + " (" + resultDownload.errorMessage + ")")
            throw new Exception(resultDownload.errorMessage)
          } else {
            LOG.info("MultitThreadDDownloadCSV.checkError ==> 5")
            LOG.info("[[DownloadCSV::main]] TENANT " + organization.getOrganizationcode + " IN PROGRESS(" + count + "/" + streamDatasets.length + ")")
          }
        }
      }
    } catch {
      case ex: Exception =>
        LOG.info("ECCEZIONE IN MultitThreadDDownloadCSV.checkError!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        LOG.info(ex.toString)
    }

    LOG.info("MultitThreadDDownloadCSV.checkError ==> END")
  }

  def executorShutdown(executor: ExecutorService) = {
    executor.shutdown();

    if (!executor.awaitTermination(4, TimeUnit.HOURS)) {
      executor.shutdownNow();
      LOG.info("[[DownloadCSV::main]]Error")
    }
  }

  def getLastMongoObjectIdByIdDatasetAndVersion(allineamentiScarico: Option[List[AllineamentoScaricoDatasetResponse]], idDataset: Integer, dsVersion: Integer): String = {
    val defaultObjectId: String = "000000000000000000000000"
    val objectId: String = allineamentiScarico match {
      case Some(allineamenti) => {
        var x = allineamenti.find(a => a.getIdDataset == idDataset && a.getDatasetVersion == dsVersion)
        if (x.isEmpty) defaultObjectId else x.get.getLastMongoObjectId
      }
      case None => defaultObjectId
    }
    objectId
  }

  def call(): Outcomes = {
    LOG.info("MultiThreadDownloadCSV.call() ==> BEGIN")
    LOG.info("PROCESS ORGANIZATION: " + organization.getOrganizationcode)

    var errorMessage = ""

    try {

      val listAllineamentoScaricoDataset = getAllineamentoByIdOrganization(organization.getIdOrganization)

      // val executor = Executors.newFixedThreadPool(2);
      val executor = Executors.newFixedThreadPool(1);
      // <<<< == per testare lo mettiamo ad 1 da ripristinare a 2

      val executorService = new ExecutorCompletionService[Outcomes](executor);

      getListStreamDataset(organization.getOrganizationcode()) match {
        case Some(streamDatasets) => {
          for (streamDataset <- streamDatasets if datasetCode.isEmpty || datasetCode.equals(streamDataset.getDataset.getDatasetcode)) {
            var lastObjectID = getLastMongoObjectIdByIdDatasetAndVersion(
                listAllineamentoScaricoDataset, streamDataset.getDataset.getIddataset, streamDataset.getVersion)
            executorService.submit(
                new SparkDatasetsDownloader(streamDataset, newObjectID, lastObjectID, sparkContext, solrDelegate, sqlContext, adminApiUrl))
          }
          checkError(executorService, streamDatasets.toBuffer)
          executorShutdown(executor)
          LOG.info("<<<<<<<<<<<<<<<<<<<<<<" + organization.getOrganizationcode + ">>>>>>>>>>>>>>> END")
        }
        case None => LOG.warn("No stream datasets for orgId" + organization.getOrganizationcode)
      }

    } catch {
      case ex: Exception => {
        LOG.error("Errore durante organization " + organization.getOrganizationcode, ex)
        errorMessage = ex.getMessage
      }
    }

    return Outcomes(organization.getOrganizationcode, errorMessage, !errorMessage.isEmpty())
  }

}

class SparkDatasetsDownloader(
  streamDataset: BackofficeDettaglioStreamDatasetResponse,
  newObjectID: String,
  lastObjectID: String,
  sparkContext: SparkContext,
  solrDelegate: SolrDelegate,
  sqlContext: SQLContext,
  adminApiUrl: String) extends Callable[Outcomes] {

  val dataTypes = immutable.Map( // much better than a switch/case ...

    "boolean" -> Seq("_b", "BOOLEAN"),
    "string" -> Seq("_s", "VARCHAR"),
    "int" -> Seq("_i", "INTEGER"),
    "long" -> Seq("_l", "BIGINT"),
    "double" -> Seq("_d", "DOUBLE"),
    "data" -> Seq("_dt", "TIMESTAMP"),
    "date" -> Seq("_dt", "TIMESTAMP"),
    "datetimeOffset" -> Seq("_dt", "TIMESTAMP"),
    "datetime" -> Seq("_dt", "TIMESTAMP"),
    "dateTime" -> Seq("_dt", "TIMESTAMP"),
    "time" -> Seq("_dt", "TIMESTAMP"),
    "float" -> Seq("_f", "FLOAT"),
    "longitude" -> Seq("_d", "DOUBLE"),
    "latitude" -> Seq("_d", "DOUBLE"),
    "binary" -> Seq("_s", "VARCHAR"),
    "bigdecimal" -> Seq("_d", "DOUBLE"));

  def merge(srcPath: String, dstPath: String): Unit = {
    val hadoopConfig = new Configuration()
    val hdfs = FileSystem.get(hadoopConfig)
    FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), true, hadoopConfig, null)
    // the "true" setting deletes the source files once they are merged into the new output
  }

  val LOG = LoggerFactory.getLogger(getClass)

  def getDsType(subType: String) = {
    val dsType = subType match {
      case "bulkDataset" => "data"
      case "socialDataset" => "social"
      case _ => "measures"
    }
    dsType
  }

  def getSolrFields(streamDataset: BackofficeDettaglioStreamDatasetResponse, dsType: String) = {
    val componentsName = getComponentsName(streamDataset)
    val fieldsNames_ALL = dsType match {
      case "measures" => Seq("time_dt", "id", "iddataset_l", "datasetversion_l") ++: componentsName
      case "social" => Seq("time_dt", "id", "iddataset_l", "datasetversion_l") ++: componentsName
      case catchItAll => Seq("id", "iddataset_l", "datasetversion_l") ++: componentsName
    }
    fieldsNames_ALL.mkString(",")
  }

  def getSubdomainCode(streamDataset: BackofficeDettaglioStreamDatasetResponse, dataDomain: String) = {
    var codSubDomain = streamDataset.getSubdomain.getSubdomaincode
    if (codSubDomain == null || codSubDomain == "") {
      codSubDomain = dataDomain
    }
    codSubDomain
  }

  def getComponentsName(streamDataset: BackofficeDettaglioStreamDatasetResponse) = {
    val components = streamDataset.getComponents.asScala
    components.map(field => field.getName.toLowerCase + dataTypes.get(field.getDataType.getDatatypecode).get(0).toLowerCase)
  }

  def getVESlug(dsType: String) = {
    var vESlug = ""

    if (dsType != "data") {

      vESlug = streamDataset.getStream.getSmartobject.getSlug

      if (vESlug != null && vESlug != "") {
        vESlug = vESlug.replaceAll("[^a-zA-Z0-9-_]", "-")
      }
    }
    vESlug
  }

  def getStreamCode(dsType: String) = {

    var streamCode = ""

    if (dsType != "data") {
      streamCode = streamDataset.getStream.getStreamcode
    }

    streamCode
  }

  def getDataFrame(idDataset: Integer, datasetVersion: Integer, solrFields: String) = {
    var df = solrDelegate.dfByIddatasetDatasetversione(sqlContext, idDataset, datasetVersion, streamDataset.getDataset.getSolrcollectionname, solrFields)
    df = df.filter(col("id").between(this.lastObjectID, this.newObjectID))

    df = df.select(
      df.columns.
        map(c => (
          if (c.endsWith("_dt"))
            date_format(to_utc_timestamp(col(c), java.util.TimeZone.getDefault.getID), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").alias(c)
          else if (c.endsWith("_s"))
            coalesce(col(c), lit("")).alias(c)
          else if (c.endsWith("_f"))
            col(c).cast(FloatType).cast(StringType).alias(c)
          else col(c))): _*)

    // Here we "drop" (aka remove...) columns we don't want in our CSV ...
    df = df.drop("id")
    df = df.drop("iddataset_l")
    df = df.drop("datasetversion_l")

    df = df.columns.foldLeft(df)((_df, _column) =>
      _df.withColumnRenamed(_column.toString, _column.toString.toLowerCase.replaceFirst("(_[^_]+$)", "")))

    df.printSchema

    df
  }

  def saveDataFrame(df: DataFrame, __csvExportPath: String) = {

    LOG.info("SparkDatasetsDownloader.saveDataFrame() ==> BEGIN")

    df.coalesce(1).write.format("com.databricks.spark.csv").
      option("header", "true").
      option("quoteMode", QuoteMode.MINIMAL.toString).
      option("treatEmptyValuesAsNulls", "false").
      // option("dateFormat", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").            // does not work (?) so we manipulate date/time df columns, above (!)
      // option("codec", "org.apache.hadoop.io.compress.GzipCodec").      // for short,  "gzip" :-)
      mode("overwrite").
      save(__csvExportPath)
    LOG.info("ThreadDatasetDownloadCSV.saveDataFrame() ==> END")
  }

  def getFinalDir(subType: String, organizationCode: String, dataDomain: String, codSubDomain: String, vESlug: String) = {
    var tempFinalDir = "/int-datalake/" + organizationCode.toUpperCase() + "/rawdata/" + dataDomain.toUpperCase() + "/"
    var finalDir = subType match {
      case "bulkDataset" => tempFinalDir + "db_" + codSubDomain.toUpperCase()
      case _ => tempFinalDir + "so_" + vESlug
    }

    finalDir
  }

  def getLastDir(subType: String, datasetCode: String, streamCode: String) = {
    var lastDir = subType match {
      case "bulkDataset" => datasetCode
      case _ => streamCode
    }

    lastDir
  }

  def mkdirs(fs: FileSystem, finalDir: String, lastDir: String) = {

    LOG.info("ThreadDatasetDownloadCSV.call() ==> BEGIN")

    LOG.info("finalDir: " + finalDir)
    LOG.info("lastDir: " + lastDir)

    if (!(fs.exists(new Path(finalDir)))) {
      fs.mkdirs(new Path(finalDir), new FsPermission("755"))
      //    fs.setOwner(new Path(finalDir), "sdp", "hdfs")
    }

    if (!(fs.exists(new Path(finalDir + "/" + lastDir)))) {
      fs.mkdirs(new Path(finalDir + "/" + lastDir), new FsPermission("750"))
      //    fs.setOwner(new Path(finalDir+"/"+lastDir), "sdp", "hdfs")
    }

    LOG.info("ThreadDatasetDownloadCSV.call() ==> END")
  }

  def createOrUpdateAllineamento(idDataset: Integer, datasetVersion: Integer, newObjectID: String) = {

    var json = s"""{ "idDataset" : $idDataset, "datasetVersion" : $datasetVersion, "lastMongoObjectId" : $newObjectID }"""

    BackOfficeCreateClient.createOrUpdateAllineamento(
      adminApiUrl, json, streamDataset.getOrganization.getIdOrganization, "ThreadDatasetDownloadCSV")
  }

  def call(): Outcomes = {

    LOG.info("ThreadDatasetDownloadCSV.call() ==> BEGIN")

    LOG.info("ThreadDatasetDownloadCSV.call() idDataset [ " + streamDataset.getDataset.getIddataset + " ]")

    var error = ""

    try {
      val datasetType = getDsType(streamDataset.getDataset.getDatasetSubtype.getDatasetSubtype)

      val countDf = solrDelegate.countDocument(
        streamDataset.getDataset.getIddataset,
        streamDataset.getVersion,
        streamDataset.getDataset.getSolrcollectionname,
        lastObjectID,
        newObjectID)

      if (countDf == 0) {
        throw new Exception(streamDataset.getDataset.getIddataset + " cannot be found on Solr i.e. COUNT(*) = 0")
      } else {
        LOG.info("NOT ZERO Mongo Size");
      }

      var dataFrame = getDataFrame(streamDataset.getDataset.getIddataset, streamDataset.getVersion, getSolrFields(streamDataset, datasetType))
      var __csvExportPath = "tmp/scarico/" + newObjectID + "/" + streamDataset.getOrganization.getOrganizationcode.toUpperCase() + "/" + "_" + f"${streamDataset.getDataset.getIddataset}%05d-${streamDataset.getVersion}%03d.csv.tmp"
      var __finalfilename = newObjectID + "_" + countDf + "-" + streamDataset.getDataset.getDatasetcode + "-" + streamDataset.getVersion + ".csv"

      saveDataFrame(dataFrame, __csvExportPath)

      var finalDir = getFinalDir(streamDataset.getDataset.getDatasetSubtype.getDatasetSubtype, streamDataset.getOrganization.getOrganizationcode, streamDataset.getDomain.getDomaincode, getSubdomainCode(streamDataset, streamDataset.getDomain.getDomaincode), getVESlug(datasetType))
      var lastDir = getLastDir(streamDataset.getDataset.getDatasetSubtype.getDatasetSubtype, streamDataset.getDataset.getDatasetcode, getStreamCode(datasetType))
      val fileSystem = FileSystem.get(sparkContext.hadoopConfiguration)

      mkdirs(fileSystem, finalDir, lastDir)

      var fileName = fileSystem.globStatus(new Path(__csvExportPath + "/part*"))(0).getPath.getName

      println("--filename:" + __csvExportPath + "/" + fileName)
      println("--new:" + finalDir + "/" + lastDir + "/" + __finalfilename)

      // DA RIPRISTINARE!!
      //        fileSystem.rename(new Path(__csvExportPath + "/" + fileName), new Path(finalDir + "/" + lastDir + "/" + __finalfilename))

      dataFrame.unpersist()

      createOrUpdateAllineamento(streamDataset.getDataset.getIddataset, streamDataset.getVersion, newObjectID)
    } catch {
      case ex: Exception => {
        LOG.error("Errore durante l'organization " + streamDataset.getOrganization.getOrganizationcode + ", dataset:" + streamDataset.getDataset.getDatasetcode, ex)
        error = ex.getMessage
      }
    }

    return Outcomes(streamDataset.getDataset.getDatasetcode + "_" + streamDataset.getVersion, error, !error.isEmpty())

  }

}

