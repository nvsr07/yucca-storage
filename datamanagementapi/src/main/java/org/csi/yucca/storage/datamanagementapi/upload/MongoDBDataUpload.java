package org.csi.yucca.storage.datamanagementapi.upload;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.types.GeoPoint;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;

import au.com.bytecode.opencsv.CSVReader;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBDataUpload {

	private ArrayList<DBObject> mongoOperation = new ArrayList<DBObject>();
	private List<SDPBulkInsertException> formalErrors = new ArrayList<SDPBulkInsertException>();

	public MongoDBDataUpload() {

	}

	public List<SDPBulkInsertException> checkFileToWrite(String datiIn, String separator, Metadata datasetMetadata, boolean skipFirstRow) {
		try {
			this.prepareDataInsert(datiIn, separator, datasetMetadata, skipFirstRow);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			formalErrors.add(new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_UNDEFINED, "-1", -1, -1, e.getMessage()));
		}
		return this.formalErrors;
	}

	/**
	 * throws an exception if there are formal errors o there are not insert
	 * operation to do
	 * 
	 * @param mongoClient
	 * @param db
	 * @param dbcollection
	 * @return
	 * @throws Exception
	 */
	public BulkWriteResult writeFileToMongo(MongoClient mongoClient, String db, String dbcollection, Metadata datasetMetadata) throws Exception {

		// TODO ... controllo errori
		if (this.formalErrors.size() > 0)
			throw new Exception("Input data contains formal Errors");

		DBCollection collection = mongoClient.getDB(db).getCollection(dbcollection);

		if (this.mongoOperation.size() > 0) {
			BulkWriteOperation bulkbuilder = collection.initializeUnorderedBulkOperation();
			for (int i = 0; i < this.mongoOperation.size(); i++) {

				DBObject dbObject = this.mongoOperation.get(i);
				dbObject.put("idDataset", datasetMetadata.getIdDataset());
				dbObject.put("datasetVersion", datasetMetadata.getDatasetVersion());

				bulkbuilder.insert(dbObject);
			}
			BulkWriteResult res = bulkbuilder.execute();

			return res;

		} else
			throw new Exception("No insert operation found");

	}

	public int getTotalDocumentToInsert() {
		return this.mongoOperation.size();
	}

	private void prepareDataInsert(String dataIn, String separator, Metadata datasetMetadata, boolean skipFirstRow) throws IOException {

		ArrayList<SDPBulkInsertException> errors = new ArrayList<SDPBulkInsertException>();

		ArrayList<DBObject> operations = new ArrayList<DBObject>();
		char separatorChar = separator.charAt(0);
		CSVReader reader = new CSVReader(new StringReader(dataIn), separatorChar);

		int numColumnFileIn = 0;
		String[] nextRow;
		int lineNumber = 0;
		while ((nextRow = reader.readNext()) != null) {
			lineNumber++;
			System.out.println("Line # " + lineNumber);
			GeoPoint geoPoint = new GeoPoint();
			String[] fieldValues = nextRow;
			String row = Util.join(nextRow, ",");
			// for (int i = 0; i < dataIn.size(); i++) {

			// String[] fieldValues = dataIn.get(i).split(separator);

			if (lineNumber == 1) { // first Row
				numColumnFileIn = fieldValues.length;
				if (skipFirstRow)
					continue;
			}

			if (fieldValues.length != numColumnFileIn) {
				SDPBulkInsertException curErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_TOTALFIELDINROW, row, lineNumber, -1, "Expected "
						+ numColumnFileIn + " columns, found " + fieldValues.length);
				errors.add(curErr);
			}

			BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
			System.out.println("start .. is empty --> " + builder.isEmpty());

			ArrayList<SDPBulkInsertException> errorCurrentRow = new ArrayList<SDPBulkInsertException>();
			for (int j = 0; datasetMetadata.getInfo().getFields() != null && j < datasetMetadata.getInfo().getFields().length; j++) {
				int numColumn = 0;
				boolean found = false;
				while (fieldValues != null && numColumn < fieldValues.length && !found) {

					String typeCode = null;
					String fieldName = null;
					DateFormat formatter = Constants.DEFAULT_FIELD_DATE_FORMAT;
					if (datasetMetadata.getInfo().getFields()[j].getSourceColumn() == (numColumn + 1)) {
						found = true;
						typeCode = datasetMetadata.getInfo().getFields()[j].getDataType();
						fieldName = datasetMetadata.getInfo().getFields()[j].getFieldName();

						if (datasetMetadata.getInfo().getFields()[j].getDateTimeFormat() != null) {
							try {
								formatter = new SimpleDateFormat(datasetMetadata.getInfo().getFields()[j].getDateTimeFormat());
							} catch (Exception e) {
								formatter = Constants.DEFAULT_FIELD_DATE_FORMAT;
							}
							formatter.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));

						}

						String curValue = fieldValues[numColumn];

						if (curValue == null || curValue.length() == 0) {
							builder.add(fieldName, null);
						} else {

							try {
								if ("int".equals(typeCode)) {
									builder.add(fieldName, new Integer(curValue));
								} else if ("long".equals(typeCode)) {
									builder.add(fieldName, new Long(curValue));
								} else if ("double".equals(typeCode)) {
									builder.add(fieldName, new Double(curValue));
								} else if ("float".equals(typeCode)) {
									builder.add(fieldName, new Float(curValue));
								} else if ("string".equals(typeCode)) {
									builder.add(fieldName, new String(curValue));
								} else if ("boolean".equals(typeCode)) {
									builder.add(fieldName, new Boolean(Boolean.parseBoolean(curValue)));
								} else if ("dateTime".equals(typeCode)) {
									builder.add(fieldName, formatter.parse(curValue));
								} else if ("longitude".equals(typeCode)) {
									Double longitude = new Double(curValue);
									builder.add(fieldName, longitude);
									geoPoint.setLongitude(longitude);
								} else if ("latitude".equals(typeCode)) {
									Double latitude = new Double(curValue);
									builder.add(fieldName, latitude);
									geoPoint.setLatitude(latitude);
								} else {
									builder.add(fieldName, new String(curValue));
								}
							} catch (Exception e) {
								SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_INVALIDTYPE, row, lineNumber,
										numColumn, "Expected value of type " + typeCode + "  found " + curValue);
								errorCurrentRow.add(curRowErr);
							}
						}

					}

					numColumn++;
				}

				if (!found) {
					SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_COLUMNNOTFOUND, row, lineNumber, -1,
							" cannot find column " + datasetMetadata.getInfo().getFields()[j].getFieldName());
					errorCurrentRow.add(curRowErr);
				}

			}
			
			if(geoPoint.isValid()){
				builder.add("idxLocation", geoPoint.getIdxLocation());
			}

			if (errorCurrentRow != null && errorCurrentRow.size() > 0) {
				errors.addAll(errorCurrentRow);
				System.out.println("end row ..errors");
			} else {
				System.out.println("end row .. is empty --> " + builder.isEmpty());
				DBObject obj = builder.get();
				System.out.println(obj);
				operations.add(obj);
			}

		}
		if (reader != null)
			reader.close();

		if (null != operations && operations.size() > 0)
			this.mongoOperation.addAll(operations);
		if (null != errors && errors.size() > 0)
			this.formalErrors.addAll(errors);

	}

}
