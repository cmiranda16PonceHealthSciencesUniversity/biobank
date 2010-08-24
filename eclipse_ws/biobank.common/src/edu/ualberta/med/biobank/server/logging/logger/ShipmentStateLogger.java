package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Date;
import java.util.Map;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Shipment;

public class ShipmentStateLogger extends BiobankObjectStateLogger {

    protected ShipmentStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj, Map<String, Object> statesMap) {
        if (obj instanceof Shipment) {
            Log log = new Log();
            String details = "";
            Date dateReceived = (Date) statesMap.get("dateReceived");
            Clinic clinic = (Clinic) statesMap.get("clinic");
            log.setSite(clinic.getSite().getNameShort());
            if (dateReceived != null) {
                details = "Received:" + dateTimeFormatter.format(dateReceived);
            }
            String waybill = (String) statesMap.get("waybill");
            if (waybill != null) {
                details += " - Waybill:" + waybill;
            }
            log.setDetails(details);
            log.setType("Shipment");
            return log;
        }
        return null;
    }

}