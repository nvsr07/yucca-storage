package org.csi.yucca.storage.datamanagementapi.importdatabase;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.exception.ImportDatabaseException;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Jdbc;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.mongodb.MongoClient;

public class DatabaseReader {

	private String organizationCode;
	private String tenantCode;
	private String dbType;
	private String dbUrl;
	private String dbName;
	private String username;
	private String password;
	private String dbSchema;

	private DatabaseConfiguration databaseConfiguation;

	List<Metadata> dataset = new LinkedList<Metadata>();
	Map<String, List<String>> columnWarnings = new HashMap<String, List<String>>();

	Map<String, String> fkMap;
	static Logger log = Logger.getLogger(DatabaseReader.class);

	public DatabaseReader(String organizationCode, String tenantCode, String dbType, String dbUrl, String dbName, String username, String password) throws ImportDatabaseException {
		super();
		log.debug("[DatabaseReader::DatabaseReader] START - dbType:  " + dbType + ", dbUrl: " + dbUrl + ", dbName: " + dbName + ", username: " + username);
		this.organizationCode = organizationCode;
		this.tenantCode = tenantCode;
		this.dbType = dbType;
		this.dbUrl = dbUrl;
		this.dbName = dbName;

		if (username != null && username.contains(":")) {
			String[] usernameSchemaDB = username.split(":");
			this.username = usernameSchemaDB[0];
			this.dbSchema = usernameSchemaDB[1];
		} else
			this.username = username;

		this.password = password;

		if (DatabaseConfiguration.DB_TYPE_HIVE.equals(dbType)) {
			this.dbUrl = "yucca_datalake";
			this.dbName = ("stg_" + organizationCode + "_" + tenantCode.replaceAll("-", "_")).toLowerCase(); // stage
			// area
			this.username = Config.getInstance().getHiveJdbcConnectionUser();
			this.password = Config.getInstance().getHiveJdbcConnectionPassword();
		}

		databaseConfiguation = DatabaseConfiguration.getDatabaseConfiguration(dbType);
		if (databaseConfiguation == null)
			throw new ImportDatabaseException(ImportDatabaseException.INVALID_DB_TYPE,
					"Database type used: " + dbType + " - Database type supported: MYSQL, ORACLE, POSTGRESQL, HIVE");

	}

	private Connection getConnection() throws ClassNotFoundException, ImportDatabaseException {
		log.debug("[DatabaseReader::getConnection] START");
		Class.forName(databaseConfiguation.getDbDriver());
		log.debug("[DatabaseReader::getConnection] Driver Loaded.");
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(databaseConfiguation.getConnectionUrl(dbUrl, dbName), username, password);
			if (dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE)) {
				((oracle.jdbc.driver.OracleConnection) connection).setIncludeSynonyms(true);
				((oracle.jdbc.driver.OracleConnection) connection).setRemarksReporting(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ImportDatabaseException(ImportDatabaseException.CONNECTION_FAILED, e.getMessage());
		}
		return connection;
	}

	public String loadSchema() throws ClassNotFoundException, ImportDatabaseException, SQLException {
		log.debug("[DatabaseReader::loadSchema] START");
		Connection conn = getConnection();
		log.debug("[DatabaseReader::loadSchema]  Got Connection.");

		Map<String, Metadata> existingMetadataMap = loadExistingMetadata();
		log.debug("[DatabaseReader::loadSchema]  existing metadata loaded.");

		DatabaseMetaData meta = conn.getMetaData();
		String[] types = { "TABLE", "VIEW", "SYNONYM" };
		ResultSet tablesResultSet = meta.getTables(null, null, "%", types);

		List<DatabaseTableDataset> tables = new LinkedList<DatabaseTableDataset>();

		if (dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE)) {
			loadFk(meta, null);
			loadFieldsMetadata(conn);

		}
		// Map<String, String> pkMap = loadPk(meta);

		//
		// TABLE_CAT String => table catalog (may be null)
		// TABLE_SCHEM String => table schema (may be null)
		// TABLE_NAME String => table name
		// TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW",
		// "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
		// "SYNONYM".
		// REMARKS String => explanatory comment on the table
		// TYPE_CAT String => the types catalog (may be null)
		// TYPE_SCHEM String => the types schema (may be null)
		// TYPE_NAME String => type name (may be null)
		// SELF_REFERENCING_COL_NAME String => name of the designated
		// "identifier" column of a typed table (may be null)
		// REF_GENERATION String => specifies how values in
		// SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER",
		// "DERIVED". (may be null)
		String hiveStageArea = ("stg_" + organizationCode + "_" + tenantCode).toLowerCase();

		while (tablesResultSet.next()) {

			String tableName = tablesResultSet.getString("TABLE_NAME");
			String tableSchema = tablesResultSet.getString("TABLE_SCHEM");
			String tableCat = tablesResultSet.getString("TABLE_CAT");
			String tableType = tablesResultSet.getString("TABLE_TYPE");
			String tableComment = tablesResultSet.getString("REMARKS");

			log.debug("[DatabaseReader::loadSchema] tableName " + tableName);

			DatabaseTableDataset table = new DatabaseTableDataset();
			table.setTableName(tableName);

			boolean checkColumOracle = !dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE)
					|| (dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE) && fieldsMetadata.get(tableName) != null);

			// System.out.println("tableType:" + tableType + ", tableSchema: " +
			// tableSchema + ", tableName: " + tableName + " tableCat: " +
			// tableCat);
			if (!tableName.equals("TOAD_PLAN_TABLE") && !tableName.equals("PLAN_TABLE") && checkColumOracle
					&& ((dbType.equals(DatabaseConfiguration.DB_TYPE_HIVE) && tableSchema.toLowerCase().startsWith(hiveStageArea))
							|| ((tableSchema == null || username.toUpperCase().equalsIgnoreCase(tableSchema))
									&& (tableCat == null || dbName.toUpperCase().equalsIgnoreCase(tableCat))))) {
				// printResultSetColumns(tablesResultSet);
				// System.out.println("tableType:" + tableType + ", tableSchema:
				// " + tableSchema + ", tableName: " + tableName + " tableCat: "
				// + tableCat);
				Field[] fields = new Field[0];
				if (!dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE)) {
					try {
						loadFk(meta, tableName);
					} catch (Exception e) {
						log.error("[DatabaseReader::loadSchema] error while loading fk  of table " + tableName + " - message: " + e.getMessage());
						e.printStackTrace();
						table.addWarning("Error loading foreign keys: " + e.getMessage());
					}
					try {
						fields = loadColumns(meta, tableName, tableSchema);
					} catch (Exception e) {
						log.error("[DatabaseReader::loadSchema] error while loading fk  of table " + tableName + " - message: " + e.getMessage());
						e.printStackTrace();
						table.addWarning("Error loading foreign keys: " + e.getMessage());
					}
				} else {
					try {
						fields = loadColumnsOracle(conn, tableName, tableSchema);
					} catch (Exception e) {
						log.error("[DatabaseReader::loadSchema] error while loading fk  of table " + tableName + " - message: " + e.getMessage());
						e.printStackTrace();
						table.addWarning("Error loading foreign keys: " + e.getMessage());
					}
				}

				for (Field field : fields) {
					if (fkMap.containsKey(tableName + "." + field.getFieldName())) {
						field.setForeignKey(fkMap.get(tableName + "." + field.getFieldName()));
					}
				}

				Metadata metadata = existingMetadataMap.get(tableName);
				if (metadata == null) {
					table.setStatus(DatabaseTableDataset.DATABASE_TABLE_DATASET_STATUS_NEW);
					table.setTableType(tableType);
					metadata = new Metadata();
					Info info = new Info();
					info.setDatasetName(tableName);
					String description = "Imported from " + dbName + " " + tableName + (tableComment != null ? " - " + tableComment : "");
					info.setDescription(description);
					info.setFields(fields);
					metadata.setInfo(info);

					Jdbc jdbc = new Jdbc();
					jdbc.setDbName(dbName);
					jdbc.setDbUrl(dbUrl);
					jdbc.setTableName(tableName);
					if (dbSchema != null)
						jdbc.setDbSchema(dbSchema);

					jdbc.setDbType(dbType);
					ConfigData configData = new ConfigData();
					configData.setJdbc(jdbc);
					metadata.setConfigData(configData);

				} else {
					table.setStatus(DatabaseTableDataset.DATABASE_TABLE_DATASET_STATUS_EXISTING);
					table.setTableType(tableType);

					List<Field> newFields = new LinkedList<Field>();
					for (int i = 0; i < fields.length; i++) {
						Field existingField = metadata.getFieldFromSourceColumnName(fields[i].getSourceColumnName());
						if (existingField == null) {
							newFields.add(fields[i]);
						} else {
							if (existingField.getSourceColumn() == null)
								existingField.setSourceColumn(i + 1);
							fields[i] = existingField;
						}
					}
					if (newFields.size() > 0) {
						// Field[] newFieldsArray = new Field[newFields.size() +
						// metadata.getInfo().getFields().length];
						// int counter = 0;
						// for (Field field : metadata.getInfo().getFields()) {
						// newFieldsArray[counter] = field;
						// counter++;
						// }
						// for (Field field : newFields) {
						// newFieldsArray[counter] = field;
						// counter++;
						// }
						metadata.getInfo().setFields(fields);
						table.setNewFields(newFields);
					}
				}

				loadPk(meta, tableName, fields);

				table.setDataset(metadata);

				if (columnWarnings.containsKey(table.getTableName())) {
					for (String warning : columnWarnings.get(table.getTableName())) {
						table.addWarning(warning);
					}
				}
				tables.add(table);
			}

		}

		Gson gson = JSonHelper.getInstance();
		String json = gson.toJson(tables);

		conn.close();

		return json;
	}

	private Map<String, Metadata> loadExistingMetadata() {
		log.debug("[DatabaseReader::loadExistingMetadata] START");
		Map<String, Metadata> existingMedatata = new HashMap<String, Metadata>();
		if (tenantCode != null) {
			MongoClient mongo;
			try {
				mongo = MongoSingleton.getMongoClient();
				String supportDb = Config.getInstance().getDbSupport();
				String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
				MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

				List<Metadata> existingMedatataList = metadataDAO.readAllMetadataByJdbc(tenantCode, dbType, dbUrl, dbName, true);
				if (existingMedatataList != null && existingMedatataList.size() > 0) {
					for (Metadata metadata : existingMedatataList) {
						if (metadata.getConfigData().getDeleted() == null || metadata.getConfigData().getDeleted() != 1)
							existingMedatata.put(metadata.getConfigData().getJdbc().getTableName(), metadata);
					}
				}

			} catch (NumberFormatException e) {
				log.error("[DatabaseReader::loadExistingMetadata] ERROR NumberFormatException " + e.getMessage());
				e.printStackTrace();
			} catch (UnknownHostException e) {
				log.error("[DatabaseReader::loadExistingMetadata] ERROR UnknownHostException " + e.getMessage());
				e.printStackTrace();
			}
		}
		return existingMedatata;

	}

	private Field[] loadColumns(DatabaseMetaData metaData, String tableName, String tableSchema) throws SQLException {
		List<Field> fields = new LinkedList<Field>();

		ResultSet columnsResultSet = null;
		try {
			columnsResultSet = metaData.getColumns(null, tableSchema, tableName, null);
			int columnCounter = 1;
			while (columnsResultSet.next()) {
				Field field = new Field();
				String columnName = columnsResultSet.getString("COLUMN_NAME");
				field.setFieldName(columnName);
				if (columnsResultSet.getString("REMARKS") != null)
					field.setFieldAlias(columnsResultSet.getString("REMARKS"));
				else
					field.setFieldAlias(columnName.replace("_", " "));

				String columnType = columnsResultSet.getString("TYPE_NAME");
				if (columnType != null)
					field.setDataType(databaseConfiguation.getTypesMap().get(columnType));
				else {
					if (!columnWarnings.containsKey(tableName))
						columnWarnings.put(tableName, new LinkedList<String>());
					columnWarnings.get(tableName).add("Unkonwn data type for column " + columnName + ": " + columnType);
					log.warn("[DatabaseReader::loadColumns] unkonwn data type  " + columnType + " for table " + tableName + " column " + columnName);
					field.setDataType("string");
				}
				field.setSourceColumn(columnCounter);
				field.setSourceColumnName(columnName);

				fields.add(field);
				columnCounter++;
			}

			Field[] fieldsArr = new Field[fields.size()];
			fieldsArr = fields.toArray(fieldsArr);

			return fieldsArr;
		} finally {
			if (columnsResultSet != null)
				columnsResultSet.close();
		}

	}

	private Map<String, List<String>> fieldsMetadata = new HashMap<String, List<String>>();

	private void loadFieldsMetadata(Connection conn) throws SQLException {
		String query = "select a.table_name, a.column_name,a.data_type,b.comments  from all_tab_columns a, all_col_comments b "
				+ "WHERE a.table_name=b.table_name AND a.column_name=b.column_name";
		if (dbSchema != null)
			query += " AND  a.owner=?";
		PreparedStatement statement = conn.prepareStatement(query);

		if (dbSchema != null)
			statement.setString(1, dbSchema.toUpperCase());

		ResultSet rs = null;
		try {
			rs = statement.executeQuery();
			while (rs.next()) {
				String tableName = rs.getString("table_name");
				String columnName = rs.getString("column_name");
				String columnComment = rs.getString("comments");
				String columnType = rs.getString("data_type");

				if (columnComment == null || columnComment.trim().equals(""))
					columnComment = columnName.replace("_", " ");
				else if (columnComment.length() > 500)
					columnComment = columnComment.substring(0, 500);

				if (columnType != null && databaseConfiguation.getTypesMap().get(columnType) != null)
					columnType = databaseConfiguation.getTypesMap().get(columnType);
				else {
					if (!columnWarnings.containsKey(tableName))
						columnWarnings.put(tableName, new LinkedList<String>());
					columnWarnings.get(tableName).add("Unkonwn data type for column " + columnName + ": " + columnType);
					log.warn("[DatabaseReader::loadFieldsMetadata] unkonwn data type  " + columnType + " for table " + tableName + " column " + columnName);
					columnType = "string";
				}

				if (!fieldsMetadata.containsKey(tableName)) {
					fieldsMetadata.put(tableName, new LinkedList<String>());
				}
				fieldsMetadata.get(tableName).add(columnName + "|" + columnComment + "|" + columnType);
			}
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private Field[] loadColumnsOracle(Connection conn, String tableName, String tableSchema) throws SQLException {
		List<Field> fields = new LinkedList<Field>();
		try {
			List<String> columns = fieldsMetadata.get(tableName);
			int counter = 0;
			for (String columnData : columns) {
				String[] columnDataSplitted = columnData.split("[|]");

				Field field = new Field();
				field.setFieldName(columnDataSplitted[0]);
				field.setFieldAlias(columnDataSplitted[1]);
				field.setDataType(columnDataSplitted[2]);

				field.setSourceColumn(counter);

				field.setSourceColumnName(columnDataSplitted[0]);

				fields.add(field);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Field[] fieldsArr = new Field[fields.size()];
		fieldsArr = fields.toArray(fieldsArr);
		return fieldsArr;

	}

	private Field[] loadColumnsFromMetadata(Connection conn, String tableName, String tableSchema) throws SQLException {
		List<Field> fields = new LinkedList<Field>();
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT * from " + tableName + " WHERE 1 = 0");
			ResultSetMetaData statementMetaData = statement.getMetaData();
			for (int i = 1; i < statementMetaData.getColumnCount() + 1; i++) {
				Field field = new Field();
				String columnName = statementMetaData.getColumnName(i);
				field.setFieldName(columnName);

				if (statementMetaData.getColumnLabel(i) != null)
					field.setFieldAlias(statementMetaData.getColumnLabel(i));
				else
					field.setFieldAlias(columnName.replace("_", " "));

				String columnType = statementMetaData.getColumnTypeName(i);
				if (columnType != null && databaseConfiguation.getTypesMap().get(columnType) != null)
					field.setDataType(databaseConfiguation.getTypesMap().get(columnType));
				else {
					log.warn("[DatabaseReader::loadColumns] unkonwn data type  " + columnType);
					field.setDataType("string");
				}
				field.setSourceColumn(i - 1);

				field.setSourceColumnName(columnName);

				fields.add(field);

			}

			Field[] fieldsArr = new Field[fields.size()];
			fieldsArr = fields.toArray(fieldsArr);

			return fieldsArr;
		} finally {
			if (statement != null)
				statement.close();
		}

	}

	private void loadFk(DatabaseMetaData metaData, String table) throws SQLException {
		ResultSet foreignKeys = null;
		try {
			fkMap = new HashMap<String, String>();

			foreignKeys = metaData.getImportedKeys(null, null, table);
			while (foreignKeys.next()) {
				String fkTableName = foreignKeys.getString("FKTABLE_NAME");
				String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
				String pkTableName = foreignKeys.getString("PKTABLE_NAME");
				String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
				// System.out.println("--" + fkTableName + "." + fkColumnName +
				// " -> " + pkTableName + "." + pkColumnName);
				fkMap.put(fkTableName + "." + fkColumnName, pkTableName + "." + pkColumnName);
			}
		} finally {
			if (foreignKeys != null)
				foreignKeys.close();
		}

	}

	private void loadPk(DatabaseMetaData metaData, String tableName, Field[] fields) throws SQLException {
		ResultSet primaryKeys = null;
		try {
			primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
			while (primaryKeys.next()) {
				String pkColumnName = primaryKeys.getString("COLUMN_NAME");
				for (Field field : fields) {
					if (field.getFieldName().equals(pkColumnName))
						field.setIsKey(1);
				}
			}
		} finally {
			if (primaryKeys != null)
				primaryKeys.close();
		}
	}

	public List<String> printResultSetColumns(ResultSet rs) {
		List<String> columnsName = new LinkedList<String>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			// The column count starts from 1
			for (int i = 1; i <= columnCount; i++) {
				String name = rsmd.getColumnName(i);
				// System.out.println("" + name + ": " + rs.getObject(name));
				columnsName.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnsName;
	}

	public static void main(String[] args) {

		String organizationCode = "organization";
		String tenantCode = "cittato-toriono";
		System.out.println(("stg_" + organizationCode + "_" + tenantCode.replaceAll("-", "_")).toLowerCase());

	}

}
