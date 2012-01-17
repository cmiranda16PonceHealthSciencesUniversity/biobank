package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private Integer statusId;
    private Collection<CommentInfo> comments;

    public static class SaveCEventSpecimenInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id;
        public String inventoryId;
        public Date createdAt;
        public Integer statusId;
        public Integer specimenTypeId;
        public Integer centerId;
        public Collection<CommentInfo> comments;
        public Double quantity;
    }

    public static class CEventAttrSaveInfo implements ActionResult {

        private static final long serialVersionUID = 1L;
        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String value;

    }

    private Collection<SaveCEventSpecimenInfo> sourceSpecimenInfos;

    private List<CEventAttrSaveInfo> ceAttrList;

    private ActionContext actionContext;

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId,
        Collection<CommentInfo> comments,
        Collection<SaveCEventSpecimenInfo> sourceSpecs,
        List<CEventAttrSaveInfo> ceAttrList) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.statusId = statusId;
        this.comments = comments;
        this.sourceSpecimenInfos = sourceSpecs;
        this.ceAttrList = ceAttrList;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Permission permission;
        if (ceventId == null) {
            permission = new CollectionEventCreatePermission(patientId);
        } else {
            permission = new CollectionEventUpdatePermission(ceventId);
        }
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {

        check(user, session);
        actionContext = new ActionContext(user, session);

        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = actionContext.load(CollectionEvent.class, ceventId);
        }

        // FIXME Version check?

        Patient patient = actionContext.load(Patient.class, patientId);
        ceventToSave.setPatient(patient);
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus(actionContext.load(ActivityStatus.class,
            statusId));

        Collection<Comment> commentsToSave = CollectionUtils.getCollection(
            ceventToSave, CollectionEventPeer.COMMENT_COLLECTION);
        CommentInfo
            .setCommentModelCollection(actionContext, commentsToSave, comments);

        setSourceSpecimens(session, ceventToSave);
        setEventAttrs(session, user, patient.getStudy(), ceventToSave);
        session.saveOrUpdate(ceventToSave);

        return new IdResult(ceventToSave.getId());
    }

    private void check(User user, Session session) {
        // Check that the visit number is unique for the patient
        List<ValueProperty<CollectionEvent>> propUple =
            new ArrayList<ValueProperty<CollectionEvent>>();
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.PATIENT.to(PatientPeer.ID), patientId));
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.VISIT_NUMBER, visitNumber));
        new UniquePreCheck<CollectionEvent>(CollectionEvent.class, ceventId,
            propUple).run(user, session);
    }

    private void setSourceSpecimens(Session session,
        CollectionEvent ceventToSave) {
        Set<Specimen> newSsCollection = new HashSet<Specimen>();

        Set<Specimen> newAllSpecCollection =
            new HashSet<Specimen>();
        Collection<Specimen> allSpecimenCollection =
            ceventToSave.getAllSpecimenCollection();
        if (allSpecimenCollection != null) {
            newAllSpecCollection.addAll(allSpecimenCollection);
        }

        Collection<Specimen> originalSpecimens =
            ceventToSave.getOriginalSpecimenCollection();

        if (sourceSpecimenInfos != null) {
            OriginInfo oi = null;

            for (SaveCEventSpecimenInfo specInfo : sourceSpecimenInfos) {
                Specimen specimen;
                if (specInfo.id == null) {
                    if (oi == null) {
                        oi = new OriginInfo();
                        oi.setCenter(actionContext.load(Center.class,
                            specInfo.centerId));
                        session.saveOrUpdate(oi);
                    }
                    specimen = new Specimen();
                    specimen.setCurrentCenter(oi.getCenter());
                    specimen.setOriginInfo(oi);
                    specimen.setTopSpecimen(specimen);
                    newAllSpecCollection.add(specimen);
                } else {
                    specimen = actionContext.load(Specimen.class, specInfo.id);

                    if (!newAllSpecCollection.contains(specimen)) {
                        throw new ActionCheckException(
                            "specimen not found in collection");
                    }
                }
                specimen.setActivityStatus(actionContext.load(
                    ActivityStatus.class, specInfo.statusId));
                specimen.setCollectionEvent(ceventToSave);
                // cascade will save-update the specimens from this list:
                specimen.setOriginalCollectionEvent(ceventToSave);
                Collection<Comment> commentsToSave = CollectionUtils
                    .getCollection(specimen,
                        SpecimenPeer.COMMENT_COLLECTION);
                CommentInfo.setCommentModelCollection(actionContext,
                    commentsToSave, specInfo.comments);
                specimen.setCreatedAt(specInfo.createdAt);
                specimen.setInventoryId(specInfo.inventoryId);
                specimen.setQuantity(specInfo.quantity);
                specimen.setSpecimenType(actionContext.load(SpecimenType.class,
                    specInfo.specimenTypeId));
                newSsCollection.add(specimen);
            }
        }

        SetDifference<Specimen> origSpecDiff = new SetDifference<Specimen>(
            originalSpecimens, newSsCollection);
        newAllSpecCollection.removeAll(origSpecDiff.getRemoveSet());
        ceventToSave.setAllSpecimenCollection(newAllSpecCollection);

        System.out.println("final cevent id " + ceventToSave.getId()
            + " original specimens size " + origSpecDiff.getNewSet().size());

        ceventToSave.setOriginalSpecimenCollection(origSpecDiff.getNewSet());
        for (Specimen srcSpc : origSpecDiff.getRemoveSet()) {
            session.delete(srcSpc);
        }

    }

    public void setEventAttrs(Session session, User user, Study study,
        CollectionEvent cevent) throws ActionException {
        Map<Integer, StudyEventAttrInfo> studyEventList =
            new StudyGetEventAttrInfoAction(
                study.getId()).run(user, session).getMap();

        Map<Integer, EventAttrInfo> ceventAttrList =
            new CollectionEventGetEventAttrInfoAction(
                ceventId).run(user, session).getMap();
        if (ceAttrList != null)
            for (CEventAttrSaveInfo attrInfo : ceAttrList) {
                EventAttrInfo ceventAttrInfo = ceventAttrList
                    .get(attrInfo.studyEventAttrId);
                StudyEventAttrInfo studyEventAttrInfo = studyEventList
                    .get(attrInfo.studyEventAttrId);

                StudyEventAttr sAttr;

                if (ceventAttrInfo != null) {
                    sAttr = ceventAttrInfo.attr.getStudyEventAttr();
                } else {
                    sAttr = studyEventAttrInfo == null ? null
                        : studyEventAttrInfo.attr;
                    if (sAttr == null) {
                        throw new ActionException(
                            "no StudyEventAttr found for id \"" //$NON-NLS-1$
                                + attrInfo.studyEventAttrId + "\""); //$NON-NLS-1$
                    }
                }

                if (!ActivityStatusEnum.ACTIVE.getId().equals(
                    sAttr.getActivityStatus().getId())) {
                    throw new ActionException(
                        "Attribute for \"" + sAttr.getGlobalEventAttr().getLabel() //$NON-NLS-1$
                            + "\" is locked, changes not premitted"); //$NON-NLS-1$
                }

                if (attrInfo.value != null) {
                    // validate the value
                    attrInfo.value = attrInfo.value.trim();
                    if (attrInfo.value.length() > 0) {
                        EventAttrTypeEnum type = attrInfo.type;
                        List<String> permissibleSplit = null;

                        if (type == EventAttrTypeEnum.SELECT_SINGLE
                            || type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                            String permissible = sAttr.getPermissible();
                            if (permissible != null) {
                                permissibleSplit = Arrays.asList(permissible
                                    .split(";")); //$NON-NLS-1$
                            }
                        }

                        if (type == EventAttrTypeEnum.SELECT_SINGLE) {
                            if (!permissibleSplit.contains(attrInfo.value)) {
                                throw new ActionException(
                                    "value " + attrInfo.value //$NON-NLS-1$
                                        + "is invalid for label \"" + sAttr.getGlobalEventAttr().getLabel() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        } else if (type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                            for (String singleVal : attrInfo.value.split(";")) { //$NON-NLS-1$
                                if (!permissibleSplit.contains(singleVal)) {
                                    throw new ActionException(
                                        "value " + singleVal + " (" //$NON-NLS-1$ //$NON-NLS-2$
                                            + attrInfo.value
                                            + ") is invalid for label \"" + sAttr.getGlobalEventAttr().getLabel() //$NON-NLS-1$
                                            + "\""); //$NON-NLS-1$
                                }
                            }
                        } else if (type == EventAttrTypeEnum.NUMBER) {
                            Double.parseDouble(attrInfo.value);
                        } else if (type == EventAttrTypeEnum.DATE_TIME) {
                            try {
                                DateFormatter.dateFormatter
                                    .parse(attrInfo.value);
                            } catch (ParseException e) {
                                throw new ActionException(e);
                            }
                        } else if (type == EventAttrTypeEnum.TEXT) {
                            // do nothing
                        } else {
                            throw new ActionException(
                                "type \"" + type + "\" not tested"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }

                EventAttr eventAttr;
                if (ceventAttrInfo == null) {
                    eventAttr = new EventAttr();
                    CollectionUtils.getCollection(cevent,
                        CollectionEventPeer.EVENT_ATTR_COLLECTION).add(
                        eventAttr);
                    eventAttr.setCollectionEvent(cevent);
                    eventAttr.setStudyEventAttr(sAttr);
                } else {
                    eventAttr = ceventAttrInfo.attr;
                }
                eventAttr.setValue(attrInfo.value);

                // FIXME need to remove attributes ? when they don't exist
                // anymore
                // in study maybe ? See previous code in wrapper ?
            }
    }

}
