package com.midshire.midshireservice;

public class PartsModel {
    public String quantity;
    public String partno;
    public String partsDescription;
    public String unitPrice;
    public String extPrice;

    public PartsModel(String quantity, String partno, String partsDescription, String unitPrice, String extPrice) {
        this.quantity = quantity;
        this.partno = partno;
        this.partsDescription = partsDescription;
        this.unitPrice = unitPrice;
        this.extPrice = extPrice;
    }

    public PartsModel() {
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPartno() {
        return partno;
    }

    public void setPartno(String partno) {
        this.partno = partno;
    }

    public String getPartsDescription() {
        return partsDescription;
    }

    public void setPartsDescription(String partsDescription) {
        this.partsDescription = partsDescription;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getExtPrice() {
        return extPrice;
    }

    public void setExtPrice(String extPrice) {
        this.extPrice = extPrice;
    }
}
