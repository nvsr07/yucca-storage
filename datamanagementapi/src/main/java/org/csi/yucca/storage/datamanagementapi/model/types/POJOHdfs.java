
package org.csi.yucca.storage.datamanagementapi.model.types;

import com.google.gson.annotations.Expose;

public class POJOHdfs {
	 @Expose
	 private FileStatuses FileStatuses;

	    public FileStatuses getFileStatuses (){
	        return FileStatuses;
	    }

	    public void setFileStatuses (FileStatuses FileStatuses){
	        this.FileStatuses = FileStatuses;
	    }

	    @Override
	    public String toString(){
	        return "POJOHdfs [FileStatuses = "+FileStatuses+"]";
	    }
}