
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class StreamTags {

    @Expose
    private List<Tag> tag = new ArrayList<Tag>();

    /**
     * 
     * @return
     *     The tag
     */
    public List<Tag> getTag() {
        return tag;
    }

    /**
     * 
     * @param tag
     *     The tag
     */
    public void setTag(List<Tag> tag) {
        this.tag = tag;
    }

}
