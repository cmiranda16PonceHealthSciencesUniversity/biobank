package edu.ualberta.med.biobank.action.patient;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.permission.patient.PatientReadPermission;

public class PatientSearchAction implements Action<SearchedPatientInfo> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr MULTIPLE_PATIENTS_FOUND =
        bundle.tr("More than one patient found with pnumber \"{0}\".");

    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_QRY =
        " SELECT p, study, COUNT(cevents)"
            + " FROM " + Patient.class.getName() + " p"
            + " LEFT JOIN p.study study"
            + " LEFT JOIN p.collectionEvents cevents"
            + " WHERE {0} GROUP BY p";

    @SuppressWarnings("nls")
    private static final String WHERE_FOR_PNUMBER = "p.pnumber=?";

    @SuppressWarnings("nls")
    private static final String WHERE_FOR_ID = "p.id=?";

    private String pnumber;
    private Integer patientId;

    public static class SearchedPatientInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Patient patient;
        public Study study;
        public Long ceventsCount;
    }

    public PatientSearchAction(String pnumber) {
        this.pnumber = pnumber;
    }

    public PatientSearchAction(Integer id) {
        this.patientId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new PatientReadPermission(patientId).isAllowed(context);
    }

    @Override
    public SearchedPatientInfo run(ActionContext context)
        throws ActionException {
        String hql = MessageFormat.format(PATIENT_INFO_QRY,
            pnumber == null ? WHERE_FOR_ID : WHERE_FOR_PNUMBER);

        Query query = context.getSession().createQuery(hql);
        query.setParameter(0, pnumber == null ? patientId : pnumber);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 0) {
            return null;
        }
        if (rows.size() == 1) {
            SearchedPatientInfo pinfo = new SearchedPatientInfo();
            Object[] row = rows.get(0);
            pinfo.patient = (Patient) row[0];
            pinfo.study = (Study) row[1];
            pinfo.ceventsCount = (Long) row[2];
            return pinfo;
        }
        throw new LocalizedException(MULTIPLE_PATIENTS_FOUND.format(pnumber));
    }
}