package org.csi.yucca.storage.datamanagementapi.importdatabase;

import java.util.HashMap;
import java.util.Map;

import org.csi.yucca.storage.datamanagementapi.importdatabase.conf.HiveConfiguration;
import org.csi.yucca.storage.datamanagementapi.importdatabase.conf.MySQLConfiguration;
import org.csi.yucca.storage.datamanagementapi.importdatabase.conf.OracleConfiguration;
import org.csi.yucca.storage.datamanagementapi.importdatabase.conf.PostgreSQLConfiguration;

public abstract class DatabaseConfiguration {

	protected String dbDriver;
	protected Map<String, String> typesMap = new HashMap<String, String>();

	public DatabaseConfiguration() {
		super();
		initTypesMap();
		initDbDriver();
	}

	protected abstract void initTypesMap();

	protected abstract String getConnectionUrl(String hostname, String dbname);

	protected abstract void initDbDriver();

	public static DatabaseConfiguration getDatabaseConfiguation(String dbType) {
		if ("MYSQL".equals(dbType))
			return new MySQLConfiguration();
		if ("ORACLE".equals(dbType))
			return new OracleConfiguration();
		if ("POSTGRESQL".equals(dbType))
			return new PostgreSQLConfiguration();
		if ("HIVE".equals(dbType))
			return new HiveConfiguration();
		return null;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public Map<String, String> getTypesMap() {
		return typesMap;
	}

}
