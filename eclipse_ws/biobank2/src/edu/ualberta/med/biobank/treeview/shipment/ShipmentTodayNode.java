package edu.ualberta.med.biobank.treeview.shipment;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
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
import edu.ualberta.med.biobank.common.action.search.SpecimenTransitSearchAction;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentTodayNode extends AbstractTodayNode<OriginInfo> {
    private static final I18n i18n = I18nFactory
        .getI18n(ShipmentTodayNode.class);

    private Boolean readAllowed;
    private Boolean addAllowed;

    @SuppressWarnings("nls")
    public ShipmentTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setLabel(i18n.tr("Today's shipments"));
        try {
            this.readAllowed = false;
            this.addAllowed = false;
            if (SessionManager.getUser().getCurrentWorkingCenter() != null) {
                this.readAllowed =
                    SessionManager.getAppService().isAllowed(
                        new OriginInfoReadPermission(SessionManager.getUser()
                            .getCurrentWorkingCenter().getWrappedObject()));
                this.addAllowed =
                    SessionManager.getAppService().isAllowed(
                        new OriginInfoUpdatePermission(SessionManager.getUser()
                            .getCurrentWorkingCenter().getId()));
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(i18n.tr("Unable to retrieve permissions"));
        }
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof ClinicWrapper);
        return new ClinicAdapter(this, (ClinicWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ClinicAdapter(this, null);
    }

    @Override
    protected List<IBiobankModel> getTodayElements()
        throws ApplicationException {
        if (SessionManager.getInstance().isConnected()
            && SessionManager.getUser().getCurrentWorkingCenter() != null
            && readAllowed) {
            SpecimenTransitSearchAction search =
                new SpecimenTransitSearchAction(SessionManager.getUser()
                    .getCurrentWorkingCenter().getId());
            search.setDateReceived(new Date());
            return SessionManager.getAppService().doAction(search).getList();
        }
        return null;
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof OriginInfoWrapper) {
            return parent.equals(((OriginInfoWrapper) child).getCenter());
        }
        return false;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, ClinicWrapper.class);
    }

    @Override
    protected void addChild(OriginInfo child) {
        SpecimenTransitView.addToNode(this, child);
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (addAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add Shipment"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addShipment();
                }
            });
        }
    }

    protected void addShipment() {
        OriginInfoWrapper shipment = new OriginInfoWrapper(
            SessionManager.getAppService());
        ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
            SessionManager.getAppService());
        shipment.setShipmentInfo(shipmentInfo);
        ShipmentAdapter shipNode = new ShipmentAdapter(SpecimenTransitView
            .getCurrent().getSearchedNode(), shipment);
        shipNode.openEntryForm();
    }

}
