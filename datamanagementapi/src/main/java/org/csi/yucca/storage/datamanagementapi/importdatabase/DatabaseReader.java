package org.csi.yucca.storage.datamanagementapi.importdatabase;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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

	private String tenantCode;
	private String dbType;
	private String dbUrl;
	private String dbName;
	private String username;
	private String password;

	private DatabaseConfiguration databaseConfiguation;

	List<Metadata> dataset = new LinkedList<Metadata>();

	Map<String, String> fkMap;
	static Logger log = Logger.getLogger(DatabaseReader.class);

	public DatabaseReader(String tenantCode, String dbType, String dbUrl, String dbName, String username, String password) throws ImportDatabaseException {
		super();
		log.debug("[DatabaseReader::DatabaseReader] START - dbType:  " + dbType + ", dbUrl: " + dbUrl + ", dbName: " + dbName + ", username: " + username);
		this.dbType = dbType;
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.username = username;
		this.password = password;
		this.tenantCode = tenantCode;

		databaseConfiguation = DatabaseConfiguration.getDatabaseConfiguation(dbType);
		if (databaseConfiguation == null)
			throw new ImportDatabaseException(ImportDatabaseException.INVALID_DB_TYPE, "Database type used: " + dbType
					+ " - Database type supported: MYSQL, ORACLE, POSTGRESQL, HIVE");

	}

	private Connection getConnection() throws ClassNotFoundException, ImportDatabaseException {
		log.debug("[DatabaseReader::getConnection] START");
		Class.forName(databaseConfiguation.getDbDriver());
		log.debug("[DatabaseReader::getConnection] Driver Loaded.");
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(databaseConfiguation.getConnectionUrl(dbUrl, dbName), username, password);
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
		String[] types = { "TABLE", "VIEW" };
		ResultSet tablesResultSet = meta.getTables(null, null, "%", types);

		List<DatabaseTableDataset> tables = new LinkedList<DatabaseTableDataset>();

		if (dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE))
			loadFk(meta, null);
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

		while (tablesResultSet.next()) {

			String tableName = tablesResultSet.getString("TABLE_NAME");
			String tableSchema = tablesResultSet.getString("TABLE_SCHEM");
			String tableCat = tablesResultSet.getString("TABLE_CAT");
			String tableType = tablesResultSet.getString("TABLE_TYPE");
			String tableComment = tablesResultSet.getString("REMARKS");

			// printResultSetColumns(tablesResultSet);

			log.debug("[DatabaseReader::loadSchema] tableName " + tableName);

			DatabaseTableDataset table = new DatabaseTableDataset();
			table.setTableName(tableName);

			if (!tableName.equals("TOAD_PLAN_TABLE") && !tableName.equals("PLAN_TABLE") && (tableSchema == null || username.toUpperCase().equalsIgnoreCase(tableSchema))
					&& (tableCat == null || dbName.toUpperCase().equalsIgnoreCase(tableCat))) {
				if (!dbType.equals(DatabaseConfiguration.DB_TYPE_ORACLE))
					loadFk(meta, tableName);

				Field[] fields = loadColumns(meta, tableName, tableSchema);
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
					if (tableComment != null)
						info.setDescription(tableComment);
					info.setFields(fields);
					metadata.setInfo(info);

					Jdbc jdbc = new Jdbc();
					jdbc.setDbName(dbName);
					jdbc.setDbUrl(dbUrl);
					jdbc.setTableName(tableName);
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

		ResultSet columnsResultSet = metaData.getColumns(null, tableSchema, tableName, null);
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
				log.warn("[DatabaseReader::loadColumns] unkonwn data type  " + columnType);
				field.setDataType("string");
			}
			field.setSourceColumnName(columnName);

			fields.add(field);
		}

		Field[] fieldsArr = new Field[fields.size()];
		fieldsArr = fields.toArray(fieldsArr);

		return fieldsArr;

	}

	private void loadFk(DatabaseMetaData metaData, String table) throws SQLException {

		fkMap = new HashMap<String, String>();

		ResultSet foreignKeys = metaData.getImportedKeys(null, null, table);
		while (foreignKeys.next()) {
			String fkTableName = foreignKeys.getString("FKTABLE_NAME");
			String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
			String pkTableName = foreignKeys.getString("PKTABLE_NAME");
			String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
			// System.out.println("--" + fkTableName + "." + fkColumnName +
			// " -> " + pkTableName + "." + pkColumnName);
			fkMap.put(fkTableName + "." + fkColumnName, pkTableName + "." + pkColumnName);
		}

	}

	private void loadPk(DatabaseMetaData metaData, String tableName, Field[] fields) throws SQLException {
		ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
		while (primaryKeys.next()) {
			String pkColumnName = primaryKeys.getString("COLUMN_NAME");
			for (Field field : fields) {
				if (field.getFieldName().equals(pkColumnName))
					field.setIsKey(1);
			}
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

}
