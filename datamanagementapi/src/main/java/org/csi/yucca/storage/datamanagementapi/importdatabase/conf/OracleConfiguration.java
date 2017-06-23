package org.csi.yucca.storage.datamanagementapi.importdatabase.conf;

import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseConfiguration;

public class OracleConfiguration extends DatabaseConfiguration {

	@Override
	protected void initTypesMap() {
		typesMap.put("VARCHAR2", "string");
		typesMap.put("NVARCHAR2", "string");
		typesMap.put("NUMBER", "int");
		typesMap.put("FLOAT", "float");
		typesMap.put("LONG", "long");
		typesMap.put("DATE", "int");
		typesMap.put("BINARY_FLOAT", "float");
		typesMap.put("BINARY_DOUBLE", "double");
		typesMap.put("TIMESTAMP", "long");
		typesMap.put("INTERVAL YEAR", "string");
		typesMap.put("INTERVAL DAY", "string");
		typesMap.put("RAW", "string");
		typesMap.put("LONG RAW", "string");
		typesMap.put("ROWID", "string");
		typesMap.put("UROWID", "string");
		typesMap.put("CHAR", "string");
		typesMap.put("NCHAR", "string");
		typesMap.put("CLOB", "string");
		typesMap.put("NCLOB", "string");
		typesMap.put("BLOB", "string");
		typesMap.put("BFILE", "string");

	}

	@Override
	protected String getConnectionUrl(String hostname, String dbname) {
		return "jdbc:oracle:thin:@" + hostname + ":" + dbname; // jdbc:oracle:thin:@localhost:1521:caspian;
	}

	@Override
	protected void initDbDriver() {
		dbDriver = "oracle.jdbc.driver.OracleDriver";
	}

}
