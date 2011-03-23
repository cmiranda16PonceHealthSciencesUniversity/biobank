package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.common.security.Feature;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm;

public class CabinetLinkAssignHandler extends LinkAssignCommonHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openLinkAssignPerspective(CabinetLinkAssignEntryForm.ID);
        return null;
    }

    @Override
    protected boolean canUserPerformAction(User user) {
        return user.canPerformActions(Feature.ASSIGN, Feature.LINK);
    }
}
