package edu.ualberta.med.biobank.common.action.info;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;

public class DispatchFormReadInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Dispatch dispatch;
    public Collection<DispatchSpecimen> specimens;

}
