package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.PvAttrType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyPvAttrWrapper extends ModelWrapper<StudyPvAttr> {

    public StudyPvAttrWrapper(WritableApplicationService appService,
        StudyPvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudyPvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "label", "permissilbe", "activityStatus",
            "pvAttrType", "study" };
    }

    @Override
    public Class<StudyPvAttr> getWrappedClass() {
        return StudyPvAttr.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the study pv attribute does not have an activity status");
        }
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (isUsedByPatientVisits()) {
            throw new BiobankCheckException("Unable to delete PvAttr with id "
                + getId() + ". A patient visit using it exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public boolean isUsedByPatientVisits() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(pva) from "
            + PvAttr.class.getName() + " as pva where pva.studyPvAttr = ?)",
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) > 0;
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = wrappedObject.getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public String getPermissible() {
        return wrappedObject.getPermissible();
    }

    public void setPermissible(String possibleValues) {
        String oldPV = wrappedObject.getPermissible();
        wrappedObject.setPermissible(possibleValues);
        propertyChangeSupport.firePropertyChange("possibleValues", oldPV,
            possibleValues);
    }

    private ActivityStatusWrapper getActivityStatusInternal() {
        ActivityStatus activityStatus = wrappedObject.getActivityStatus();
        if (activityStatus == null)
            return null;
        return new ActivityStatusWrapper(appService, activityStatus);
    }

    public String getActivityStatus() {
        ActivityStatusWrapper activityStatus = getActivityStatusInternal();
        if (activityStatus == null) {
            return null;
        }
        return activityStatus.getName();
    }

    private void setActivityStatus(ActivityStatus activityStatus) {
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);

    }

    public void setActivityStatus(String name) throws Exception {
        ActivityStatusWrapper activityStatus = ActivityStatusWrapper
            .getActivityStatus(appService, name);
        if (activityStatus == null) {
            throw new Exception("activity status \"" + name + "\" is invalid");
        }
        setActivityStatus(activityStatus.getWrappedObject());
    }

    public PvAttrTypeWrapper getPvAttrType() {
        PvAttrType type = wrappedObject.getPvAttrType();
        if (type == null) {
            return null;
        }
        return new PvAttrTypeWrapper(appService, type);
    }

    public void setPvAttrType(PvAttrType type) {
        PvAttrType oldType = wrappedObject.getPvAttrType();
        wrappedObject.setPvAttrType(type);
        propertyChangeSupport.firePropertyChange("pvAttrType", oldType, type);
    }

    public void setPvAttrType(PvAttrTypeWrapper type) {
        setPvAttrType(type.getWrappedObject());
    }

    public StudyWrapper getStudy() {
        Study study = wrappedObject.getStudy();
        if (study == null) {
            return null;
        }
        return new StudyWrapper(appService, study);
    }

    public void setStudy(Study study) {
        Study oldStudy = wrappedObject.getStudy();
        wrappedObject.setStudy(study);
        propertyChangeSupport.firePropertyChange("study", oldStudy, study);
    }

    public void setStudy(StudyWrapper study) {
        setStudy(study.getWrappedObject());
    }

    @Override
    public int compareTo(ModelWrapper<StudyPvAttr> o) {
        return 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":\"" + getLabel() + "\":\"" + getPermissible()
            + "\":" + getActivityStatus() + ":" + getPvAttrType().getName()
            + ":" + getStudy();
    }
}
