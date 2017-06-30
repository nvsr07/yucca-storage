package org.csi.yucca.storage.datamanagementapi.importdatabase.conf;

import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseConfiguration;

public class PostgreSQLConfiguration extends DatabaseConfiguration {

	@Override
	protected void initTypesMap() {
		typesMap.put("bigint", "long");
		typesMap.put("int8", "long");
		typesMap.put("bigserial", "long");
		typesMap.put("serial8", "long");
		typesMap.put("bit", "int");
		typesMap.put("bit varying", "int");
		typesMap.put("varbit", "int");
		typesMap.put("boolean", "boolean");
		typesMap.put("bool", "boolean");
		typesMap.put("box", "string");
		typesMap.put("bytea", "int");
		typesMap.put("character", "string");
		typesMap.put("char", "string");
		typesMap.put("character varying", "string");
		typesMap.put("varchar", "string");
		typesMap.put("cidr", "string");
		typesMap.put("circle", "string");
		typesMap.put("date", "dateTime");
		typesMap.put("double precision", "double");
		typesMap.put("float8", "double");
		typesMap.put("inet", "string");
		typesMap.put("int4", "int");
		typesMap.put("int", "int");
		typesMap.put("integer", "int");
		typesMap.put("interval", "string");
		typesMap.put("json", "string");
		typesMap.put("line", "string");
		typesMap.put("lseg", "string");
		typesMap.put("macaddr", "string");
		typesMap.put("money", "string");
		typesMap.put("numeric", "int");
		typesMap.put("decimal", "int");
		typesMap.put("path", "string");
		typesMap.put("point", "string");
		typesMap.put("polygon", "string");
		typesMap.put("real", "float");
		typesMap.put("float4", "float");
		typesMap.put("smallint", "int");
		typesMap.put("int2", "int");
		typesMap.put("smallserial", "int");
		typesMap.put("serial2", "int");
		typesMap.put("serial", "string");
		typesMap.put("serial4", "string");
		typesMap.put("text", "string");
		typesMap.put("time", "dateTime");
		typesMap.put("timetz", "dateTime");
		typesMap.put("timestamp", "long");
		typesMap.put("timestamptz", "long");
		typesMap.put("tsquery", "string");
		typesMap.put("tsvector", "string");
		typesMap.put("txid_snapshot", "string");
		typesMap.put("uuid", "string");
		typesMap.put("xml", "string");


	}

	@Override
	protected String getConnectionUrl(String hostname, String dbname) {
		return "jdbc:postgresql://" + hostname + "/" + dbname; // jdbc:postgresql://hostname:port/dbname
	}

	@Override
	protected void initDbDriver() {
		dbDriver = "org.postgresql.Driver";
	}

}
