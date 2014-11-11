
package org.csi.yucca.storage.datamanagementapi.model.api;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class MyApi {

    @Expose
    private Integer idApi;
    @Expose
    private String apiName;
    @Expose
    private String apiDescription;
    @Expose
    private ConfigData configData;
    @Expose
    private List<Dataset> dataset = new ArrayList<Dataset>();

    /**
     * 
     * @return
     *     The idApi
     */
    public Integer getIdApi() {
        return idApi;
    }

    /**
     * 
     * @param idApi
     *     The idApi
     */
    public void setIdApi(Integer idApi) {
        this.idApi = idApi;
    }

    /**
     * 
     * @return
     *     The apiName
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * 
     * @param apiName
     *     The apiName
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    /**
     * 
     * @return
     *     The apiDescription
     */
    public String getApiDescription() {
        return apiDescription;
    }

    /**
     * 
     * @param apiDescription
     *     The apiDescription
     */
    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }

    /**
     * 
     * @return
     *     The configData
     */
    public ConfigData getConfigData() {
        return configData;
    }

    /**
     * 
     * @param configData
     *     The configData
     */
    public void setConfigData(ConfigData configData) {
        this.configData = configData;
    }

    /**
     * 
     * @return
     *     The dataset
     */
    public List<Dataset> getDataset() {
        return dataset;
    }

    /**
     * 
     * @param dataset
     *     The dataset
     */
    public void setDataset(List<Dataset> dataset) {
        this.dataset = dataset;
    }

}
