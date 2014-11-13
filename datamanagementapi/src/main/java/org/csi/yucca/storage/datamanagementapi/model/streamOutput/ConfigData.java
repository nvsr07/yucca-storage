
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class ConfigData {

    @Expose
    private Integer idTenant;
    @Expose
    private String tenantCode;
    @Expose
    private Long idDataset;
    @Expose
    private Integer datasetVersion;

    /**
     * 
     * @return
     *     The idTenant
     */
    public Integer getIdTenant() {
        return idTenant;
    }

    /**
     * 
     * @param idTenant
     *     The idTenant
     */
    public void setIdTenant(Integer idTenant) {
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

}
