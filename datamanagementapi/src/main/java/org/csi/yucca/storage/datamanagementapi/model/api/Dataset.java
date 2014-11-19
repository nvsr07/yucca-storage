
package org.csi.yucca.storage.datamanagementapi.model.api;

import com.google.gson.annotations.Expose;

public class Dataset {

    @Expose
    private Long idDataset;
    @Expose
    private Integer datasetVersion;
    @Expose
    private Long idTenant;
    @Expose
    private String tenantCode;
    @Expose
    private Integer idStream;
    @Expose
    private String streamCode;
    @Expose
    private String virtualEntityCode;

    /**
     * 
     * @return
     *     The idDataset
     */
    public Long getIdDataset() {
        return idDataset;
    }

    /**
     * 
     * @param idDataset
     *     The idDataset
     */
    public void setIdDataset(Long idDataset) {
        this.idDataset = idDataset;
    }

    /**
     * 
     * @return
     *     The datasetVersion
     */
    public Integer getDatasetVersion() {
        return datasetVersion;
    }

    /**
     * 
     * @param datasetVersion
     *     The datasetVersion
     */
    public void setDatasetVersion(Integer datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    /**
     * 
     * @return
     *     The idTenant
     */
    public Long getIdTenant() {
        return idTenant;
    }

    /**
     * 
     * @param idTenant
     *     The idTenant
     */
    public void setIdTenant(Long idTenant) {
        this.idTenant = idTenant;
    }

    /**
     * 
     * @return
     *     The tenantCode
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 
     * @param tenantCode
     *     The tenantCode
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    /**
     * 
     * @return
     *     The idStream
     */
    public Integer getIdStream() {
        return idStream;
    }

    /**
     * 
     * @param idStream
     *     The idStream
     */
    public void setIdStream(Integer idStream) {
        this.idStream = idStream;
    }

    /**
     * 
     * @return
     *     The streamCode
     */
    public String getStreamCode() {
        return streamCode;
    }

    /**
     * 
     * @param streamCode
     *     The streamCode
     */
    public void setStreamCode(String streamCode) {
        this.streamCode = streamCode;
    }

    /**
     * 
     * @return
     *     The virtualEntityCode
     */
    public String getVirtualEntityCode() {
        return virtualEntityCode;
    }

    /**
     * 
     * @param virtualEntityCode
     *     The virtualEntityCode
     */
    public void setVirtualEntityCode(String virtualEntityCode) {
        this.virtualEntityCode = virtualEntityCode;
    }

}
