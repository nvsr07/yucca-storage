package org.csi.yucca.storage.datamanagementapi.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;

public abstract class DataUpload {

	protected List<SDPBulkInsertException> formalErrors = new ArrayList<SDPBulkInsertException>();

	public List<SDPBulkInsertException> checkFileToWrite(String datiIn, String separator, Metadata datasetMetadata, boolean skipFirstRow) {
		try {
			this.prepareDataInsert(datiIn, separator, datasetMetadata, skipFirstRow);
		} catch (IOException e) {
			e.printStackTrace();
			formalErrors.add(new SDPBulkInsertException(SDPBulkInsertException.ERROR_TYPE_UNDEFINED, "-1", -1, -1, e.getMessage()));
		}
		return this.formalErrors;
	}
	
	
	protected abstract void prepareDataInsert(String dataIn, String separator, Metadata datasetMetadata, boolean skipFirstRow) throws IOException;


	public abstract int getTotalDocumentToInsert();


	public abstract void writeData(String tenant, Metadata datasetMetadata) throws Exception;


	public abstract void prepareHeader(Metadata datasetMetadata);

}
