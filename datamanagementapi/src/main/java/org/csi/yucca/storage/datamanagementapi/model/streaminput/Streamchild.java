
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import com.google.gson.annotations.Expose;

public class Streamchild {

    @Expose
    private Integer idStream;
    @Expose
    private Integer idChildStream;
    @Expose
    private String nomeTenant;
    @Expose
    private String codiceTenant;
    @Expose
    private String virtualEntityName;
    @Expose
    private String virtualEntityDescription;
    @Expose
    private String codiceStream;
    @Expose
    private String nomeStream;
    @Expose
    private String codiceVirtualEntity;
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
     *     The nomeTenant
     */
    public String getNomeTenant() {
        return nomeTenant;
    }

    /**
     * 
     * @param nomeTenant
     *     The nomeTenant
     */
    public void setNomeTenant(String nomeTenant) {
        this.nomeTenant = nomeTenant;
    }

    /**
     * 
     * @return
     *     The codiceTenant
     */
    public String getCodiceTenant() {
        return codiceTenant;
    }

    /**
     * 
     * @param codiceTenant
     *     The codiceTenant
     */
    public void setCodiceTenant(String codiceTenant) {
        this.codiceTenant = codiceTenant;
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
    public String getVirtualEntityDescription() {
        return virtualEntityDescription;
    }

    /**
     * 
     * @param virtualEntityDescription
     *     The virtualEntityDescription
     */
    public void setVirtualEntityDescription(String virtualEntityDescription) {
        this.virtualEntityDescription = virtualEntityDescription;
    }

    /**
     * 
     * @return
     *     The codiceStream
     */
    public String getCodiceStream() {
        return codiceStream;
    }

    /**
     * 
     * @param codiceStream
     *     The codiceStream
     */
    public void setCodiceStream(String codiceStream) {
        this.codiceStream = codiceStream;
    }

    /**
     * 
     * @return
     *     The nomeStream
     */
    public String getNomeStream() {
        return nomeStream;
    }

    /**
     * 
     * @param nomeStream
     *     The nomeStream
     */
    public void setNomeStream(String nomeStream) {
        this.nomeStream = nomeStream;
    }

    /**
     * 
     * @return
     *     The codiceVirtualEntity
     */
    public String getCodiceVirtualEntity() {
        return codiceVirtualEntity;
    }

    /**
     * 
     * @param codiceVirtualEntity
     *     The codiceVirtualEntity
     */
    public void setCodiceVirtualEntity(String codiceVirtualEntity) {
        this.codiceVirtualEntity = codiceVirtualEntity;
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
