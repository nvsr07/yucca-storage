
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import com.google.gson.annotations.Expose;

public class Tag {

    @Expose
    private String tagCode;

    /**
     * 
     * @return
     *     The tagCode
     */
    public String getTagCode() {
        return tagCode;
    }

    /**
     * 
     * @param tagCode
     *     The tagCode
     */
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

}
