package org.csi.yucca.storage.datamanagementapi.importdatabase.conf;

import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseConfiguration;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;


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
		typesMap.put("TIMESTAMP", "dateTime");
		typesMap.put("DATE", "dateTime");
		typesMap.put("INTERVAL", "string");
		typesMap.put("STRING", "string");
		typesMap.put("VARCHAR", "string");
		typesMap.put("CHAR", "string");
		typesMap.put("BOOLEAN", "boolean");
		
		
	}

	@Override
	protected String getConnectionUrl(String hostname, String dbname) {
		return Config.getInstance().getHiveJdbcConnectionUrl();
	}

	@Override
	protected void initDbDriver() {
		//dbDriver = "org.apache.hadoop.hive.jdbc.HiveDriver";
		dbDriver = "org.apache.hive.jdbc.HiveDriver";
	}

}
