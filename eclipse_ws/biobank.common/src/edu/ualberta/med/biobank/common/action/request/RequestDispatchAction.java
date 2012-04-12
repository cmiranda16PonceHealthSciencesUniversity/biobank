package edu.ualberta.med.biobank.common.action.request;

import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.permission.request.UpdateRequestPermission;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.Specimen;

public class RequestDispatchAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 89092566507468524L;
    private DispatchSaveInfo dInfo;
    private List<Integer> specs;
    private RequestSpecimenState rsstate;
    private Integer requestId;
    private Set<DispatchSpecimenInfo> dspecs;

    public RequestDispatchAction(Integer requestId, List<Integer> specs,
        RequestSpecimenState rsstate,
        DispatchSaveInfo dInfo, Set<DispatchSpecimenInfo> dspecs) {
        this.specs = specs;
        this.dInfo = dInfo;
        this.dspecs = dspecs;
        this.rsstate = rsstate;
        this.requestId = requestId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UpdateRequestPermission(specs)
            .isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        // Dispatch is saved here because it is all one transaction
        RequestStateChangeAction stateaction =
            new RequestStateChangeAction(specs, rsstate);
        stateaction.run(context);
        Request request = context.get(Request.class, requestId);
        Dispatch d = context.load(Dispatch.class, dInfo.id, new Dispatch());
        d.setId(dInfo.id);
        d.setReceiverCenter(context.load(Center.class, dInfo.receiverId));
        d.setSenderCenter(context.load(Center.class, dInfo.senderId));
        d.setState(dInfo.state);

        request.getDispatches().add(d);
        context.getSession().saveOrUpdate(request);
        context.getSession().flush();

        for (DispatchSpecimenInfo ds : dspecs) {
            DispatchSpecimen dispatchSpecimen = new DispatchSpecimen();
            dispatchSpecimen.setId(ds.id);
            dispatchSpecimen.setDispatch(d);
            dispatchSpecimen.setSpecimen(context.load(Specimen.class,
                ds.specimenId));
            dispatchSpecimen.setState(ds.state);
            context.getSession().saveOrUpdate(dispatchSpecimen);
        }

        return new EmptyResult();
    }
}
