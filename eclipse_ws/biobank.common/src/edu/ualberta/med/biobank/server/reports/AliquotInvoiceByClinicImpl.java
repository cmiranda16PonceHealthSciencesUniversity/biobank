package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotInvoiceByClinicImpl extends AbstractReport {

    private static String QUERY_STRING =
        "Select Alias.inventoryId, Alias.patientVisit.shipmentPatient.shipment.clinic.name, "
            + "Alias.patientVisit.shipmentPatient.patient.pnumber, "
            + "Alias.linkDate, Alias.sampleType.name  from "
            + Aliquot.class.getName()
            + " as Alias left join Alias.aliquotPosition p where (p is null or p not in (from "
            + AliquotPosition.class.getName()
            + " a where a.container.label like '"
            + SENT_SAMPLES_FREEZER_NAME
            + "')) and Alias.linkDate between ? and ? and "
            + "Alias.patientVisit.shipmentPatient.shipment.site.id "
            + SITE_OPERATOR
            + SITE_ID
            + " ORDER BY "
            + "Alias.patientVisit.shipmentPatient.shipment.clinic.id, Alias.patientVisit.shipmentPatient.patient.pnumber, Alias.inventoryId";

    public AliquotInvoiceByClinicImpl(BiobankReport report) {
        super(QUERY_STRING, report);
    }

}
