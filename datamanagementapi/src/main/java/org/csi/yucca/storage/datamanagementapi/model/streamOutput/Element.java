
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class Element {

    @Expose
    private Integer idComponent;
    @Expose
    private String componentName;
    @Expose
    private String componentAlias;
    @Expose
    private Double tolerance;
    @Expose
    private Integer idMeasureUnit;
    @Expose
    private String measureUnit;
    @Expose
    private String measureUnitCategory;
    @Expose
    private Integer idPhenomenon;
    @Expose
    private String phenomenon;
    @Expose
    private String phenomenonCategory;
    @Expose
    private String dataType;
    @Expose
    private Integer idDataType;

    /**
     * 
     * @return
     *     The idComponent
     */
    public Integer getIdComponent() {
        return idComponent;
    }

    /**
     * 
     * @param idComponent
     *     The idComponent
     */
    public void setIdComponent(Integer idComponent) {
        this.idComponent = idComponent;
    }

    /**
     * 
     * @return
     *     The componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * 
     * @param componentName
     *     The componentName
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * 
     * @return
     *     The componentAlias
     */
    public String getComponentAlias() {
        return componentAlias;
    }

    /**
     * 
     * @param componentAlias
     *     The componentAlias
     */
    public void setComponentAlias(String componentAlias) {
        this.componentAlias = componentAlias;
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

}
