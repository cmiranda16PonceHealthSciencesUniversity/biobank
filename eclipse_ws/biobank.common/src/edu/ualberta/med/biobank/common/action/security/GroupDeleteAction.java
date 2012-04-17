package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Group;

public class GroupDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final GroupDeleteInput input;

    public GroupDeleteAction(GroupDeleteInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagerPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Group group = context.load(Group.class, input.getGroupId());

        if (!group.isFullyManageable(context.getUser()))
            throw new ActionException(
                LString.tr("You do not have adequate permissions to delete this group"));

        context.getSession().delete(group);
        return new EmptyResult();
    }
}
