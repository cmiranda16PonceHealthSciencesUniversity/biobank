package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenBatchOpGetAction
    implements Action<SpecimenBatchOpGetResult> {
    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("nls")
    private static final String FILE_META_DATA_QRY = "SELECT bo.input.metaData " +
        " FROM " + BatchOperation.class.getName() + " bo" +
        " WHERE bo.id = ?";
    
    @SuppressWarnings("nls")
    private static final String SPECIMEN_QRY = "SELECT bos.specimen " +
        " FROM " + BatchOperationSpecimen.class.getName() + " bos" +
        " WHERE bos.batch.id = ?";

    private final Integer id;

    public SpecimenBatchOpGetAction(Integer batchOperationId) {
        this.id = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public SpecimenBatchOpGetResult run(ActionContext context)
        throws ActionException {
        Session session = context.getSession();

        BatchOperation batch = context.load(BatchOperation.class, id);

        FileMetaData meta = (FileMetaData) session
            .createQuery(FILE_META_DATA_QRY)
            .setParameter(0, id)
            .uniqueResult();

        @SuppressWarnings("unchecked")
        List<Specimen> specimens = session
            .createQuery(SPECIMEN_QRY)
            .setParameter(0, id)
            .list();

        SpecimenBatchOpGetResult result =
            new SpecimenBatchOpGetResult(batch, meta, specimens);

        return result;
    }
}
