package edu.ualberta.med.biobank.action.csvimport.specimen;

import java.util.Date;

import edu.ualberta.med.biobank.action.csvimport.ICsvInfo;

/**
 * POJO used by SuperCSV for CSV import feature.
 * 
 * @author loyola
 * 
 */
public class SpecimenCsvInfo implements ICsvInfo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String inventoryId;
    private String parentInventoryID;
    private String specimenType;
    private Date createdAt;
    private String patientNumber;
    private Integer visitNumber;
    private String waybill;
    private Boolean sourceSpecimen = false;
    private String worksheet;
    private String palletProductBarcode;
    private String rootContainerType;
    private String palletLabel;
    private String palletPosition;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getParentInventoryId() {
        return parentInventoryID;
    }

    public void setParentInventoryId(String parentInventoryID) {
        this.parentInventoryID = parentInventoryID;
    }

    public String getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createAt) {
        this.createdAt = createAt;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public Boolean getSourceSpecimen() {
        return sourceSpecimen;
    }

    public void setSourceSpecimen(Boolean sourceSpecimen) {
        this.sourceSpecimen = sourceSpecimen;
    }

    public String getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(String worksheet) {
        this.worksheet = worksheet;
    }

    public String getPalletProductBarcode() {
        return palletProductBarcode;
    }

    public void setPalletProductBarcode(String palletProductBarcode) {
        this.palletProductBarcode = palletProductBarcode;
    }

    public String getRootContainerType() {
        return rootContainerType;
    }

    public void setRootContainerType(String rootContainerType) {
        this.rootContainerType = rootContainerType;
    }

    public String getPalletLabel() {
        return palletLabel;
    }

    public void setPalletLabel(String palletLabel) {
        this.palletLabel = palletLabel;
    }

    public String getPalletPosition() {
        return palletPosition;
    }

    public void setPalletPosition(String palletPosition) {
        this.palletPosition = palletPosition;
    }

}