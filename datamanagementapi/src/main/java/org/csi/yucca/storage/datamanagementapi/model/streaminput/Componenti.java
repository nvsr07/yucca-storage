
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Componenti {

    @Expose
    private List<Element> element = new ArrayList<Element>();

    /**
     * 
     * @return
     *     The element
     */
    public List<Element> getElement() {
        return element;
    }

    /**
     * 
     * @param element
     *     The element
     */
    public void setElement(List<Element> element) {
        this.element = element;
    }

}
