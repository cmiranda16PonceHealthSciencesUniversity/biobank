package edu.ualberta.med.biobank.common.action.batchoperation.shipment;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class ShipmentBatchOpInputRow implements IBatchOpInputPojo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private Date dateReceived;
    private String sendingCenter;
    private String receivingCenter;
    private String shippingMethod;
    private String waybill;
    private String comment;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getSendingCenter() {
        return sendingCenter;
    }

    public void setSendingCenter(String sendingCenter) {
        this.sendingCenter = sendingCenter;
    }

    public String getReceivingCenter() {
        return receivingCenter;
    }

    public void setReceivingCenter(String receivingCenter) {
        this.receivingCenter = receivingCenter;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
