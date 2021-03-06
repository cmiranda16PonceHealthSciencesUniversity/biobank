package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class OutgoingNode extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(OutgoingNode.class);

    private final InCreationDispatchGroup creationNode;
    private final SentInTransitDispatchGroup sentTransitNode;
    private final Boolean createAllowed;

    @SuppressWarnings("nls")
    public OutgoingNode(AdapterBase parent, int id) {
        super(parent, id, i18n.tr("Outgoing"), true);
        creationNode = new InCreationDispatchGroup(this, 0);
        creationNode.setParent(this);
        addChild(creationNode);

        sentTransitNode = new SentInTransitDispatchGroup(this, 1);
        sentTransitNode.setParent(this);
        addChild(sentTransitNode);

        this.createAllowed = isAllowed(
            new DispatchCreatePermission(SessionManager.getUser()
                .getCurrentWorkingCenter().getId()));
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add Dispatch"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addDispatch();
                }
            });
        }
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public void rebuild() {
        for (AbstractAdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    public void addDispatch() {
        creationNode.addDispatch();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
