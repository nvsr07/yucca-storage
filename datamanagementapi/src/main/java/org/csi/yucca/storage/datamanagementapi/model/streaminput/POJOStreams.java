
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import com.google.gson.annotations.Expose;

public class POJOStreams {
	 @Expose
	    private Streams streams;

	    /**
	     * 
	     * @return
	     *     The streams
	     */
	    public Streams getStreams() {
	        return streams;
	    }

	    /**
	     * 
	     * @param streams
	     *     The streams
	     */
	    public void setStreams(Streams streams) {
	        this.streams = streams;
	    }

	}
