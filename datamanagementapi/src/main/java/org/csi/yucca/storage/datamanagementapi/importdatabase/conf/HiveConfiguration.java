package org.csi.yucca.storage.datamanagementapi.importdatabase.conf;

import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseConfiguration;

public class HiveConfiguration extends DatabaseConfiguration {

	@Override
	protected void initTypesMap() {
		typesMap.put("TINYINT", "int");
		typesMap.put("SMALLINT", "int");
		typesMap.put("INT", "int");
		typesMap.put("INTEGER", "int");
		typesMap.put("BIGINT", "int");
		typesMap.put("FLOAT", "float");
		typesMap.put("DOUBLE", "double");
		typesMap.put("DOUBLE PRECISION", "double");
		typesMap.put("DECIMAL", "double");
		typesMap.put("NUMERIC", "double");
		typesMap.put("TIMESTAMP", "long");
		typesMap.put("DATE", "dateTime");
		typesMap.put("INTERVAL", "string");
		typesMap.put("STRING", "string");
		typesMap.put("VARCHAR", "string");
	}

	@Override
	protected String getConnectionUrl(String hostname, String dbname) {
		
		return "jdbc:hive2://" + hostname + "/" + dbname; // "jdbc:hive2://localhost:10000/default"
	}

	@Override
	protected void initDbDriver() {
		dbDriver = "org.apache.hadoop.hive.jdbc.HiveDriver";
	}

}