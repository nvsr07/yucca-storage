
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class StreamTags {

    @Expose
    private List<Tag> tags = new ArrayList<Tag>();

    /**
     * 
     * @return
     *     The tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     *     The tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
