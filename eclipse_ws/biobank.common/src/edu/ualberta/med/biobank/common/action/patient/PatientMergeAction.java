package edu.ualberta.med.biobank.common.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.patient.PatientMergePermission;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * Merge patient2 into patient1.
 * 
 */
public class PatientMergeAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private final Integer patient1Id;
    private final Integer patient2Id;

    private final String comment;

    public PatientMergeAction(Integer patient1Id, Integer patient2Id,
        String comment) {
        this.patient1Id = patient1Id;
        this.patient2Id = patient2Id;
        this.comment = comment;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new PatientMergePermission(patient1Id, patient2Id).isAllowed(
            context);
    }

    @SuppressWarnings("nls")
    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        Session session = context.getSession();

        if ((comment == null) || comment.trim().isEmpty()) {
            throw new ActionException("comment cannot be null or empty");
        }

        Patient patient1 = context.load(Patient.class, patient1Id);
        Patient patient2 = context.load(Patient.class, patient2Id);
        if (!patient1.getStudy().equals(patient2.getStudy())) {
            throw new PatientMergeException();
        }

        Set<CollectionEvent> c1events = patient1.getCollectionEvents();
        Set<CollectionEvent> c2events = patient2.getCollectionEvents();
        if (!c2events.isEmpty()) {
            boolean merged = false;
            // for loop on a different list.
            for (CollectionEvent c2event : new ArrayList<CollectionEvent>(c2events)) {
                for (CollectionEvent c1event : c1events) {
                    merged = merge(session, c1event, c2event);
                    if (merged)
                        break;
                }
                if (!merged) {
                    // the collection has not been merged, so we can add it
                    // as a new collection event in patient1
                    c1events.add(c2event);
                    c2events.remove(c2event);
                    c2event.setPatient(patient1);
                }
                merged = false;
            }
        }

        // merge all comments from old patient into new patient
        Set<Comment> commentsToMerge = CommentUtil.copyCommentsFromCollection(
            patient2.getComments());
        for (Comment comment : commentsToMerge) {
            session.save(comment);
        }
        patient1.getComments().addAll(commentsToMerge);

        Comment c = CommentUtil.create(context.getUser(), comment);
        patient1.getComments().add(c);
        context.getSession().saveOrUpdate(patient1);

        // flush so deleting the patient realizes its collection events have
        // been removed.
        context.getSession().flush();

        context.getSession().delete(patient2);

        // FIXME see how logs should be done properly...
        Log logP2 = new Log();
        logP2.setAction("merge");
        logP2.setPatientNumber(patient2.getPnumber());
        logP2.setDetails(patient2.getPnumber() + " --> " + patient1.getPnumber());
        logP2.setType("Patient");
        context.getSession().save(logP2);

        Log logP1 = new Log();
        logP1.setAction("merge");
        logP1.setPatientNumber(patient1.getPnumber());
        logP1.setDetails(patient1.getPnumber() + " <-- " + patient2.getPnumber());
        logP1.setType("Patient");
        context.getSession().save(logP1);

        return new BooleanResult(true);
    }

    /**
     * merge 2 collection event if their visitNumber are identical.
     * 
     * @return true if a merge has been made, otherwise false
     */
    private boolean merge(Session session, CollectionEvent c1event, CollectionEvent c2event) {
        if (!c1event.getVisitNumber().equals(c2event.getVisitNumber()))  return false;

        Collection<Specimen> c2Origspecs = c2event.getOriginalSpecimens();
        Collection<Specimen> c2AllSpecs = c2event.getAllSpecimens();

        Collection<Specimen> c1OrigSpecs = c1event.getOriginalSpecimens();
        Collection<Specimen> c1AllSpecs = c1event.getAllSpecimens();

        for (Specimen spec : c2AllSpecs) {
            if (c2Origspecs.contains(spec)) {
                spec.setOriginalCollectionEvent(c1event);
                c1OrigSpecs.add(spec);
            }
            spec.setCollectionEvent(c1event);
            c1AllSpecs.add(spec);
        }

        Set<Comment> commentsToMerge = CommentUtil.copyCommentsFromCollection(
            c2event.getComments());
        for (Comment comment : commentsToMerge) {
            session.save(comment);
        }
        c1event.getComments().addAll(commentsToMerge);

        // because of this and the move of specimens into another cevent, we
        // can't use delete-orphan
        session.delete(c2event);
        return true;
    }
}
