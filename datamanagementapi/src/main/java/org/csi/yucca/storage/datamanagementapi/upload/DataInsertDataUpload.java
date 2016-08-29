package org.csi.yucca.storage.datamanagementapi.upload;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import org.csi.yucca.storage.datamanagementapi.delegate.HttpDelegate;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.tenantin.TenantIn;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;

public class DataInsertDataUpload extends DataUpload {

	String items = "[";
	int totalCount = 0;

	@Override
	protected void prepareDataInsert(String dataIn, String separator, Metadata datasetMetadata, boolean skipFirstRow) throws IOException {

		ArrayList<SDPBulkInsertException> errors = new ArrayList<SDPBulkInsertException>();

		char separatorChar = separator.charAt(0);
		CSVReader reader = new CSVReader(new StringReader(dataIn), separatorChar);

		int numColumnFileIn = 0;
		String[] nextRow;
		int lineNumber = 0;
		String item = "{\"datasetCode\":\"" + datasetMetadata.getDatasetCode() + "\",\"datasetVersion\":\"" + datasetMetadata.getDatasetVersion() + "\", \"values\": [";
		
		while ((nextRow = reader.readNext()) != null) {
			lineNumber++;
			System.out.println("Line # " + lineNumber);
			// GeoPoint geoPoint = new GeoPoint();
			String[] fieldValues = nextRow;
			String row = Util.join(nextRow, ",");
			if (lineNumber == 1) { // first Row
				numColumnFileIn = fieldValues.length;
				if (skipFirstRow)
					continue;
			}

			if (fieldValues.length != numColumnFileIn) {
				SDPBulkInsertException curErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_TOTALFIELDINROW, row, lineNumber, -1, "Expected " + numColumnFileIn
						+ " columns, found " + fieldValues.length);
				errors.add(curErr);
			}

			String values = "";
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
							values += "\"" + fieldName + "\":  null,";
						} else {

							try {
								if ("int".equals(typeCode) || "long".equals(typeCode) || "double".equals(typeCode) || "float".equals(typeCode) || "longitude".equals(typeCode) || "latitude".equals(typeCode))
									values += "\"" + fieldName + "\":" + curValue + ",";
								else
									values += "\"" + fieldName + "\":\"" + curValue + "\",";

							} catch (Exception e) {
								SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_INVALIDTYPE, row, lineNumber, numColumn,
										"Expected value of type " + typeCode + "  found " + curValue);
								errorCurrentRow.add(curRowErr);
							}
						}

					}

					numColumn++;
				}
				
				if (!found) {
					SDPBulkInsertException curRowErr = new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_COLUMNNOTFOUND, row, lineNumber, -1, " cannot find column "
							+ datasetMetadata.getInfo().getFields()[j].getFieldName());
					errorCurrentRow.add(curRowErr);
				}

			}

			if (errorCurrentRow != null && errorCurrentRow.size() > 0) {
				errors.addAll(errorCurrentRow);
				System.out.println("end row ..errors");
			} else {
				item += "{" + values.substring(0, values.length() - 1) + "}, ";
			}

		}

		item = item.substring(0, item.length() - 2) + "]},";
		items += item;
		totalCount++;
		items = items.substring(0, items.length() - 1) + "]";
		if (reader != null)
			reader.close();

		if (null != errors && errors.size() > 0)
			this.formalErrors.addAll(errors);

	}

	@Override
	public int getTotalDocumentToInsert() {
		return totalCount;
	}

	@Override
	public void writeData(String tenantCode, Metadata datasetMetadata) throws Exception {

		String tenantDetailUrl = Config.getInstance().getApiAdminServiceUrl() + "/tenants/" + tenantCode;
		String tenantDetailString = HttpDelegate.executeGet(tenantDetailUrl, null, null, null);
		Gson gson = JSonHelper.getInstance();
		TenantIn tenantin = gson.fromJson(tenantDetailString, TenantIn.class);
		String tenantPassword = tenantin.getTenants().getTenant().getTenantPassword();

		String insertApiUrl = Config.getInstance().getDataInsertBaseUrl() + tenantCode;

		String executePost = HttpDelegate.executePost(insertApiUrl, tenantCode, tenantPassword, null, null, null, items);
		System.out.println("resutl: " + executePost);
	}

}