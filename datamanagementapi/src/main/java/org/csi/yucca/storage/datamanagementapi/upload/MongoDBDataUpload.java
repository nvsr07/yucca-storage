package org.csi.yucca.storage.datamanagementapi.upload;

import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;

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

	public List<SDPBulkInsertException> checkFileToWrite(List<String> datiIn, String separatore, Metadata datasetMetadata, boolean skipFirstRow){
		this.prepareDataInsert(datiIn, separatore, datasetMetadata, skipFirstRow);
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
			// System.out.println("DATA IN          --> "+datiIn.length);
			System.out.println("******************************InsertOperations --> " + this.mongoOperation.size());
			System.out.println("******************************InsertResults    --> " + res.getInsertedCount());

			return res;

		} else
			throw new Exception("No insert operation found");

	}

	public int getTotalDocumentToInsert() {
		return this.mongoOperation.size();
	}

	private void prepareDataInsert(List<String> datiIn, String separatore, Metadata datasetMetadata, boolean skipFirstRow) {

		ArrayList<SDPBulkInsertException> errori = new ArrayList<SDPBulkInsertException>();

		ArrayList<DBObject> operazioni = new ArrayList<DBObject>();
		int numColonneFileIn = 0;
		for (int i = 0; i < datiIn.size(); i++) {

			String[] valoriCampi = datiIn.get(i).split(separatore);

			if (i == 0) { // first Row
				numColonneFileIn = valoriCampi.length;
				if (skipFirstRow)
					continue;
			}

			if (valoriCampi.length != numColonneFileIn) {
				SDPBulkInsertException curErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_TOTALFIELDINROW, datiIn.get(i), i, -1, "Expected "
						+ numColonneFileIn + " columns, found " + valoriCampi.length);
				errori.add(curErr);
			}

			// int numColonna=0;
			BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
			System.out.println("inizio .. is empty --> " + builder.isEmpty());

			ArrayList<SDPBulkInsertException> erroriCurrRow = new ArrayList<SDPBulkInsertException>();
			for (int j = 0; datasetMetadata.getInfo().getFields() != null && j < datasetMetadata.getInfo().getFields().length; j++) {
				int numColonna = 0;
				boolean found = false;
				while (valoriCampi != null && numColonna < valoriCampi.length && !found) {
					String typeCode = null;
					String fieldName = null;
					if (Integer.parseInt(datasetMetadata.getInfo().getFields()[j].getSourceColumn()) == (numColonna + 1)) {
						found = true;
						typeCode = datasetMetadata.getInfo().getFields()[j].getDataType();
						fieldName = datasetMetadata.getInfo().getFields()[j].getFieldName();
						String curValue = valoriCampi[numColonna];

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

								} else {
									builder.add(fieldName, new String(curValue));
								}
							} catch (Exception e) {
								SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_INVALIDTYPE, datiIn.get(i), i,
										numColonna, "Expected value of type " + typeCode + "  found " + curValue);
								erroriCurrRow.add(curRowErr);
							}
						}

					}

					numColonna++;
				}

				if (!found) {
					SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_COLUMNNOTFOUND, datiIn.get(i), i, -1,
							" cannot find column " + datasetMetadata.getInfo().getFields()[j].getFieldName());
					erroriCurrRow.add(curRowErr);
				}

			}

			if (erroriCurrRow != null && erroriCurrRow.size() > 0) {
				errori.addAll(erroriCurrRow);
				System.out.println("fine riga ..errori");
			} else {
				System.out.println("fine riga .. is empty --> " + builder.isEmpty());
				DBObject obj = builder.get();
				System.out.println(obj);
				operazioni.add(obj);
			}

			// for (int numColonna=0;valoriCampi!=null &&
			// numColonna<valoriCampi.length; numColonna++) {
			// String curValue=valoriCampi[numColonna];
			// String typeCode=null;
			// String fieldName=null;
			// for (int j=0;datasetMetadata.getInfo().getFields()!=null &&
			// j<datasetMetadata.getInfo().getFields().length;j++ ) {
			// if
			// (Integer.parseInt(datasetMetadata.getInfo().getFields()[j].getSourceColumn())==(numColonna+1))
			// {
			// typeCode=datasetMetadata.getInfo().getFields()[j].getDataType();
			// fieldName=datasetMetadata.getInfo().getFields()[j].getFieldName();
			// }
			// }
			//
			// if (null!=typeCode && null!=fieldName) {
			// if ("int".equals(typeCode)) {
			// builder.add(fieldName, new Integer(curValue));
			// } else if ("long".equals(typeCode)) {
			// builder.add(fieldName, new Long(curValue));
			// } else if ("double".equals(typeCode)) {
			// builder.add(fieldName, new Double(curValue));
			// } else if ("float".equals(typeCode)) {
			// builder.add(fieldName, new Float(curValue));
			// } else if ("string".equals(typeCode)) {
			// builder.add(fieldName, new String(curValue));
			// } else if ("boolean".equals(typeCode)) {
			// builder.add(fieldName, new Boolean(curValue));
			//
			// } else {
			// builder.add(fieldName, new String(curValue));
			// }
			// }
			// }

		}
		// System.out.println("DATA IN          --> "+datiIn.length);
		// System.out.println("InsertOperations --> "+operazioni.size());

		// System.out.println("InsertOperations --> "+operazioni.size());
		// for (int i=0;i<operazioni.size();i++) {
		// System.out.println("      ->"+operazioni.get(i));
		// }
		//
		// System.out.println("Errori controlli --> "+errori.size());
		// for (int i=0;i<errori.size();i++) {
		// System.out.println("      ->"+errori.get(i).toString());
		// }

		if (null != operazioni && operazioni.size() > 0)
			this.mongoOperation.addAll(operazioni);
		if (null != errori && errori.size() > 0)
			this.formalErrors.addAll(errori);

		// if (operazioni.size()>0) {
		// BulkWriteOperation bulkbuilder =
		// collection.initializeUnorderedBulkOperation();
		// for (int i=0;i<operazioni.size();i++) {
		// bulkbuilder.insert(operazioni.get(i));
		// }
		// BulkWriteResult res=bulkbuilder.execute();
		// System.out.println("DATA IN          --> "+datiIn.length);
		// System.out.println("InsertOperations --> "+operazioni.size());
		// System.out.println("InsertResults    --> "+res.getInsertedCount());
		//
		//
		// }

	}

}
