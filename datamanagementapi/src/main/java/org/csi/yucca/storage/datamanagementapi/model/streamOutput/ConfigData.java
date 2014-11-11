
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class ConfigData {

    @Expose
    private Integer idTenant;
    @Expose
    private String tenantCode;
    @Expose
    private String collection;
    @Expose
    private String type;
    @Expose
    private String subtype;
    @Expose
    private String entityNameSpace;
    @Expose
    private Integer idDataset;
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
     *     The collection
     */
    public String getCollection() {
        return collection;
    }

    /**
     * 
     * @param collection
     *     The collection
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The subtype
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * 
     * @param subtype
     *     The subtype
     */
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * 
     * @return
     *     The entityNameSpace
     */
    public String getEntityNameSpace() {
        return entityNameSpace;
    }

    /**
     * 
     * @param entityNameSpace
     *     The entityNameSpace
     */
    public void setEntityNameSpace(String entityNameSpace) {
        this.entityNameSpace = entityNameSpace;
    }

    /**
     * 
     * @return
     *     The idDataset
     */
    public Integer getIdDataset() {
        return idDataset;
    }

    /**
     * 
     * @param idDataset
     *     The idDataset
     */
    public void setIdDataset(Integer idDataset) {
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
