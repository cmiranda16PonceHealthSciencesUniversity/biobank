package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addStudy";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager.searchNode(site);
        Assert.isNotNull(siteAdapter);
        StudyWrapper study = new StudyWrapper(SessionManager.getAppService());
        StudyAdapter studyNode = new StudyAdapter(SessionManager.getInstance()
            .getSession(), study);
        studyNode.openEntryForm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(StudyWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}