
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class Streamchild {

    @Expose
    private Integer idStream;
    @Expose
    private Integer idChildStream;
    @Expose
    private String streamCode;
    @Expose
    private String streamName;
    @Expose
    private Integer idTenant;
    @Expose
    private String tenantCode;
    @Expose
    private String tenantName;
    @Expose
    private String virtualEntityCode;
    @Expose
    private String virtualEntityName;
    @Expose
    private Object virtualEntityDescription;
    @Expose
    private String internalQuery;
    @Expose
    private String aliasChildStream;

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
     *     The idChildStream
     */
    public Integer getIdChildStream() {
        return idChildStream;
    }

    /**
     * 
     * @param idChildStream
     *     The idChildStream
     */
    public void setIdChildStream(Integer idChildStream) {
        this.idChildStream = idChildStream;
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
     *     The streamName
     */
    public String getStreamName() {
        return streamName;
    }

    /**
     * 
     * @param streamName
     *     The streamName
     */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

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
     *     The tenantName
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 
     * @param tenantName
     *     The tenantName
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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

    /**
     * 
     * @return
     *     The virtualEntityName
     */
    public String getVirtualEntityName() {
        return virtualEntityName;
    }

    /**
     * 
     * @param virtualEntityName
     *     The virtualEntityName
     */
    public void setVirtualEntityName(String virtualEntityName) {
        this.virtualEntityName = virtualEntityName;
    }

    /**
     * 
     * @return
     *     The virtualEntityDescription
     */
    public Object getVirtualEntityDescription() {
        return virtualEntityDescription;
    }

    /**
     * 
     * @param virtualEntityDescription
     *     The virtualEntityDescription
     */
    public void setVirtualEntityDescription(Object virtualEntityDescription) {
        this.virtualEntityDescription = virtualEntityDescription;
    }

    /**
     * 
     * @return
     *     The internalQuery
     */
    public String getInternalQuery() {
        return internalQuery;
    }

    /**
     * 
     * @param internalQuery
     *     The internalQuery
     */
    public void setInternalQuery(String internalQuery) {
        this.internalQuery = internalQuery;
    }

    /**
     * 
     * @return
     *     The aliasChildStream
     */
    public String getAliasChildStream() {
        return aliasChildStream;
    }

    /**
     * 
     * @param aliasChildStream
     *     The aliasChildStream
     */
    public void setAliasChildStream(String aliasChildStream) {
        this.aliasChildStream = aliasChildStream;
    }

}
