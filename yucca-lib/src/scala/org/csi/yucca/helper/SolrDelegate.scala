package org.csi.yucca.helper

import org.slf4j.LoggerFactory

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.SparkContext
import org.bson.Document
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import com.lucidworks.spark.util.SolrQuerySupport
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse

class SolrDelegate(val connectionUrl: String) {
  val configuration = Map(
   "zkhost" -> connectionUrl 
  )
  val LOG = LoggerFactory.getLogger(getClass)

  def dfByIddatasetDatasetversione(sqlContext: SQLContext, idDataset: Integer, datasetVersion: Integer, collection: String, fields: String): DataFrame = {
    synchronized {
    
    LOG.info("[[SolrDelegate::dfByIddatasetDatasetversione]] Starting get solr")
    var options = configuration ++ Map ( "collection" -> collection,
      "rows" -> "8000000",
      "fields" -> fields
     )
     if (null!=fields) {
        options = options ++ Map ( "collection" -> collection,
          "fields" -> fields
       )
     }
     
    
    return sqlContext.read.format("org.csi.yucca.storageutils.helper.solrformat").options(options).load().filter(col("iddataset_l").===(idDataset) && col("datasetversion_l").===(datasetVersion))
    }

  }

  def countDocument(idDataset: Integer, datasetVersion: Integer, collection: String, minId: String, maxId: String): Long ={
    
    var solrquery = new SolrQuery;
    solrquery.addFilterQuery("iddataset_l:"+idDataset.toString())
    if (datasetVersion>0) solrquery.addFilterQuery("datasetversion_l:"+datasetVersion.toString())
    if (null!=minId ) solrquery.addFilterQuery("id:["+minId+" TO "+maxId+"]")
    
    solrquery.setQuery("*:*")
    
    LOG.info("--->"+solrquery.toQueryString()+"|"+solrquery.toString())
    
    return SolrQuerySupport.getNumDocsFromSolr(collection, configuration.getOrElse(
        "zkhost",connectionUrl ),
        Option(solrquery))
  }
 
  
  def deleteDocsForPromotion (idDataset: Integer,datasetVersion: Integer,collection: String) : Long = {
    val server = new CloudSolrClient(connectionUrl)
    val queryDelete="iddataset_l:"+idDataset.toString()  + " AND origin_s:datalake"
    LOG.info("[[SolrDelegate::deleteDocsForPromotion]] query--->"+queryDelete+ " on collection : "+queryDelete)
    server.setDefaultCollection(collection)
    val retcode:UpdateResponse = server.deleteByQuery(collection, queryDelete )
    val retcode2:UpdateResponse = server.commit()
    return 1
  }
  
  
}

