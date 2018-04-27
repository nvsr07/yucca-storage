package it.csi.prodotto.componente

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable
import scala.collection.mutable.Buffer

import org.apache.commons.lang.time.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.bson.types.ObjectId
import org.csi.yucca.adminapi.client.BackofficeListaClient
import org.csi.yucca.adminapi.response.OrganizationResponse
import org.csi.yucca.helper.SolrDelegate
import org.slf4j.LoggerFactory

object csvDownloadApp {

  val ADMIN_API_URL = ""

  val ZK_HOSTS_URL = ""

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

  val LOG = LoggerFactory.getLogger(getClass)

  def setSystemProperties() = {
    System.setProperty("java.security.auth.login.config", "jaas-client.conf");
    System.setProperty("spark.driver.allowMultipleContexts", "true");
    System.setProperty("solr.jaas.conf.path", "jaas-client.conf");
  }

  def createObjectId() = {
    new ObjectId(DateUtils.addMinutes(new java.util.Date, -7)).toString()
  }

  def getSparkContext() = {
    //    val conf2 = new SparkConf().setMaster("yarn-client").set("spark.executor.instances", "8")
    //      .set("spark.yarn.queue", "produzione").setExecutorEnv(Array(("java.security.auth.login.config", "jaas-client.conf")));
    val conf2 = new SparkConf().setMaster("yarn-client").set("spark.executor.instances", "6")
      .set("spark.executor.memory", "6g")
      .set("spark.yarn.queue", "produzione").setExecutorEnv(Array(("java.security.auth.login.config", "jaas-client.conf")));

    //val conf2 = new SparkConf().set("spark.yarn.queue","produzione").setExecutorEnv(Array(("java.security.auth.login.config", "jaas-client.conf")));

    new SparkContext(conf2)
  }

  def getSqlContextHive(sparkContext: SparkContext) = {
    new org.apache.spark.sql.hive.HiveContext(sparkContext)
  }

  def getOrganization(adminApiUrl: String) = {
    val organizations = BackofficeListaClient.getOrganizations(adminApiUrl, csvDownloadApp.toString())
    LOG.info("Trovate " + organizations.size() + " organizations.")
    organizations
  }

  def checkError(
    executorCompletionService: ExecutorCompletionService[Outcomes],
    organizations: Buffer[OrganizationResponse], config: Config) = {

    LOG.info("DownloadCSV.checkError() ==> BEGIN")

    var exit = 0

    for (organization <- organizations if config.organizations.isEmpty || (config.organizations contains organization.getOrganizationcode)) {
      val resultDownload = executorCompletionService.take().get();

      if (resultDownload.hasErrors) {
        LOG.error("[[DownloadCSV::main]] ERROR on Tenant " + resultDownload.tenantCode + " (" + resultDownload.errorMessage + ")")
        exit = -1
      } else {
        LOG.info("[[DownloadCSV::main]] TENANT FINISHED = " + resultDownload.tenantCode)
      }
    }

    LOG.info("DownloadCSV.checkError() ==> END")
    exit

  }

  def executorShutdownAndSystemExit(executor: ExecutorService) = {
    executor.shutdown();

    if (!executor.awaitTermination(4, TimeUnit.HOURS)) {
      executor.shutdownNow();
      LOG.info("[[DownloadCSV::main]]Error")
      System.exit(-1);
    }
  }

  def getSQLContext(sparkContext: SparkContext) = {
    val sqlContext = new SQLContext(sparkContext)
    sqlContext.udf.register("toTOString", (f: Float) => f.toString)
    sqlContext
  }

  case class Config(
    organizations: Seq[String] = Seq(),
    datasetCode: String = null,
    adminApiUrl: String = ADMIN_API_URL,
    zkHosts: String = ZK_HOSTS_URL)

  val _appName = getClass.getName // should we include package name ?!

  val parser = new scopt.OptionParser[Config](_appName) {

    head(_appName, "1.0")

    opt[Seq[String]]("organizations").valueName("[organization1 [ organization2 [...]]]").optional().unbounded().action((x, config) =>
      config.copy(organizations = x)).text("opt list of orgCodes to download")

    opt[String]("admin-api-uri").valueName("<adminApiURI>").action((x, config) =>
      config.copy(adminApiUrl = x)).text("adminApi connection string")

    opt[String]("zkHosts").valueName("<zkHosts>").action((x, config) =>
      config.copy(zkHosts = x)).text("list of Solr 'failovering' hosts")

    opt[String]("datasetCode").valueName("<datasetCode> (required one org)").optional().action((x, config) =>
      config.copy(datasetCode = x)).text("Optional datasetCode to download (requires one org)")

    checkConfig(c =>
      if ((c.datasetCode != null) &&
        c.organizations.length != 1) failure("If use datasetCode you must choose ONE organization")
      else success)
  }

  def getConfig(args: Array[String]): Config = {
    parser.parse(args, Config()) match {
      case Some(config) => config
      case None => {
        throw new Exception("[[DownloadCSV::main]] Error conf")
      }
    }
  }

  def main(args: Array[String]) = {

    LOG.info("#############################################################à")
    LOG.info("[[DownloadCSV::main]]BEGIN")
    LOG.info("#############################################################à")

    val config: Config = getConfig(args)

    setSystemProperties()

    val organizations = getOrganization(config.adminApiUrl).asScala
    val sparkContext = getSparkContext()
    val sqlContextHive = getSqlContextHive(sparkContext)

    sparkContext.hadoopConfiguration.set("mapred.output.compress", "false")

    //    val executor                  = Executors.newFixedThreadPool(4);
    val executor = Executors.newFixedThreadPool(1);
    // <<<< == per testare lo mettiamo ad 1 da ripristinare a 4

    val executorCompletionService = new ExecutorCompletionService[Outcomes](executor);

    LOG.info("Thread Executor created!")

    val solrDelegate = new SolrDelegate(config.zkHosts + "/solr")
    val sqlContext = getSQLContext(sparkContext)
    val newObjectId = createObjectId()

    LOG.info("Created new Object Id: " + newObjectId)

    for (
      organization <- organizations if config.organizations.nonEmpty && config.organizations.contains(organization.getOrganizationcode)
    ) {
      executorCompletionService.submit(
        new MT_dataSetsDownloader(organization, config.datasetCode, newObjectId, sparkContext, solrDelegate, sqlContext, config.adminApiUrl))
    }

    var exit = checkError(executorCompletionService, organizations, config)

    executorShutdownAndSystemExit(executor)
    LOG.info("[[DownloadCSV::main]]END")
    System.exit(exit);

    LOG.info("[[DownloadCSV::main]]EXIT")

  }

}

