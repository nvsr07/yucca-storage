package org.csi.yucca.storage.datamanagementapi.importdatabase.conf;

import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseConfiguration;

public class MySQLConfiguration extends DatabaseConfiguration {

	@Override
	protected void initTypesMap() {
		typesMap.put("INT", "int");
		typesMap.put("TINYINT", "int");
		typesMap.put("SMALLINT", "int");
		typesMap.put("MEDIUMINT", "int");
		typesMap.put("BIGINT", "long");
		typesMap.put("FLOAT", "float");
		typesMap.put("DOUBLE", "double");
		typesMap.put("DECIMAL", "double");
		typesMap.put("DATE", "dateTime");
		typesMap.put("DATETIME", "dateTime");
		typesMap.put("TIMESTAMP", "long");
		typesMap.put("TIME", "long");
		typesMap.put("YEAR", "int");
		typesMap.put("CHAR", "string");
		typesMap.put("VARCHAR", "string");
		typesMap.put("BLOB", "string");
		typesMap.put("TEXT", "string");
		typesMap.put("TINYBLOB", "string");
		typesMap.put("TINYTEXT", "string");
		typesMap.put("MEDIUMBLOB", "string");
		typesMap.put("MEDIUMTEXT", "string");
		typesMap.put("LONGBLOB", "string");
		typesMap.put("LONGTEXT", "string");
		typesMap.put("ENUM", "int");
		typesMap.put("BIT", "int");
		typesMap.put("INT UNSIGNED", "int");
		typesMap.put("TINYINT UNSIGNED", "int");
		typesMap.put("SMALLINT UNSIGNED", "int");
		typesMap.put("MEDIUMINT UNSIGNED", "int");
		typesMap.put("BIGINT UNSIGNED", "long");
	}

	@Override
	protected String getConnectionUrl(String hostname, String dbname) {
		return "jdbc:mysql://" + hostname + "/" + dbname; // jdbc:mysql://hostname:port/dbname
	}

	@Override
	protected void initDbDriver() {
		dbDriver = "com.mysql.jdbc.Driver";
	}

}
