
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class Streams {

    @Expose
    private Stream stream;

    /**
     * 
     * @return
     *     The stream
     */
    public Stream getStream() {
        return stream;
    }

    /**
     * 
     * @param stream
     *     The stream
     */
    public void setStream(Stream stream) {
        this.stream = stream;
    }

}
