
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class StreamInternalChildren {

    @Expose
    private List<Streamchild> streamChildren = new ArrayList<Streamchild>();

    /**
     * 
     * @return
     *     The streamChildren
     */
    public List<Streamchild> getStreamChildren() {
        return streamChildren;
    }

    /**
     * 
     * @param streamChildren
     *     The streamChildren
     */
    public void setStreamChildren(List<Streamchild> streamChildren) {
        this.streamChildren = streamChildren;
    }

}
