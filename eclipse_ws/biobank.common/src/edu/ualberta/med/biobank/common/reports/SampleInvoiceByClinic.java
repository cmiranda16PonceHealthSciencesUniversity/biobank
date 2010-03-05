package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.Date;

import edu.ualberta.med.biobank.model.Aliquot;

public class SampleInvoiceByClinic extends QueryObject {

    protected static final String NAME = "Aliquots per Clinic by Date";

    private static String QUERY_STRING = "Select Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Aliquot.class.getName()
        + " as Alias where Alias.linkDate > ? and Alias.linkDate < ? and "
        + "Alias.patientVisit.patient.study.site.id {1} {0} ORDER BY "
        + "Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber";

    public SampleInvoiceByClinic(String op, Integer siteId) {
        super(
            "Lists all samples linked in a particular date range, ordered by clinic.",
            MessageFormat.format(QUERY_STRING, siteId, op), new String[] {
                "Clinic", "Patient Number", "Link Date", "Aliquot Type" },
            new int[] { 100, 200, 100, 100 });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
