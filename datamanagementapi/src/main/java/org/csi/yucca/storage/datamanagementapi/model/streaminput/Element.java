
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Element {

    @Expose
    private String nome;
    @Expose
    private Double tolerance;
    @Expose
    private Integer idComponente;
    @Expose
    private String measureUnit;
    @Expose
    private String measureUnitCategory;
    @Expose
    private String phenomenon;
    @Expose
    private String phenomenonCategory;
    @Expose
    private String dataType;
    @Expose
    private Integer idPhenomenon;
    @Expose
    private Integer idDataType;
    @Expose
    private Integer idMeasureUnit;

    /**
     * 
     * @return
     *     The nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * 
     * @param nome
     *     The nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * 
     * @return
     *     The tolerance
     */
    public Double getTolerance() {
        return tolerance;
    }

    /**
     * 
     * @param tolerance
     *     The tolerance
     */
    public void setTolerance(Double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * 
     * @return
     *     The idComponente
     */
    public Integer getIdComponente() {
        return idComponente;
    }

    /**
     * 
     * @param idComponente
     *     The idComponente
     */
    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    /**
     * 
     * @return
     *     The measureUnit
     */
    public String getMeasureUnit() {
        return measureUnit;
    }

    /**
     * 
     * @param measureUnit
     *     The measureUnit
     */
    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    /**
     * 
     * @return
     *     The measureUnitCategory
     */
    public String getMeasureUnitCategory() {
        return measureUnitCategory;
    }

    /**
     * 
     * @param measureUnitCategory
     *     The measureUnitCategory
     */
    public void setMeasureUnitCategory(String measureUnitCategory) {
        this.measureUnitCategory = measureUnitCategory;
    }

    /**
     * 
     * @return
     *     The phenomenon
     */
    public String getPhenomenon() {
        return phenomenon;
    }

    /**
     * 
     * @param phenomenon
     *     The phenomenon
     */
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    /**
     * 
     * @return
     *     The phenomenonCategory
     */
    public String getPhenomenonCategory() {
        return phenomenonCategory;
    }

    /**
     * 
     * @param phenomenonCategory
     *     The phenomenonCategory
     */
    public void setPhenomenonCategory(String phenomenonCategory) {
        this.phenomenonCategory = phenomenonCategory;
    }

    /**
     * 
     * @return
     *     The dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 
     * @param dataType
     *     The dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 
     * @return
     *     The idPhenomenon
     */
    public Integer getIdPhenomenon() {
        return idPhenomenon;
    }

    /**
     * 
     * @param idPhenomenon
     *     The idPhenomenon
     */
    public void setIdPhenomenon(Integer idPhenomenon) {
        this.idPhenomenon = idPhenomenon;
    }

    /**
     * 
     * @return
     *     The idDataType
     */
    public Integer getIdDataType() {
        return idDataType;
    }

    /**
     * 
     * @param idDataType
     *     The idDataType
     */
    public void setIdDataType(Integer idDataType) {
        this.idDataType = idDataType;
    }

    /**
     * 
     * @return
     *     The idMeasureUnit
     */
    public Integer getIdMeasureUnit() {
        return idMeasureUnit;
    }

    /**
     * 
     * @param idMeasureUnit
     *     The idMeasureUnit
     */
    public void setIdMeasureUnit(Integer idMeasureUnit) {
        this.idMeasureUnit = idMeasureUnit;
    }

}
