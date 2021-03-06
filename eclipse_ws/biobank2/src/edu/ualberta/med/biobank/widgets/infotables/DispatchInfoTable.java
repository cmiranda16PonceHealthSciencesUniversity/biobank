package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchDeletePermission;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchInfoTable extends InfoTableWidget<Dispatch> {
    public static final I18n i18n = I18nFactory
        .getI18n(DispatchInfoTable.class);

    protected static class TableRowData {
        Dispatch dispatch;
        String sender;
        Date dispatchTime;
        String receiver;
        Date dateReceived;
        String waybill;
        String dstatus;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { sender,
                    DateFormatter.formatAsDate(dispatchTime), receiver,
                    DateFormatter.formatAsDate(dateReceived), waybill,
                    dstatus }, "\t");
        }
    }

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Sender"),
        i18n.tr("Dispatch Time"),
        i18n.tr("Receiver"),
        i18n.tr("Date Received"),
        ShipmentInfo.PropertyName.WAYBILL.toString(),
        i18n.tr("Dispatch State") };

    private final boolean editMode = false;

    public DispatchInfoTable(Composite parent,
        List<Dispatch> dispatchesRaw) {
        super(parent, null, HEADINGS, 15, DispatchWrapper.class);
        setList(dispatchesRaw);
    }

    @Override
    protected boolean isEditMode() {
        return editMode;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return info.sender;
                case 1:
                    return DateFormatter.formatAsDate(info.dispatchTime);
                case 2:
                    return info.receiver;
                case 3:
                    return DateFormatter.formatAsDate(info.dateReceived);
                case 4:
                    return info.waybill;
                case 5:
                    return info.dstatus;
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.dispatch = (Dispatch) obj;
        info.sender = info.dispatch.getSenderCenter().getNameShort();
        info.dispatchTime =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
                .getShipmentInfo().getPackedAt();
        info.receiver = info.dispatch.getReceiverCenter().getNameShort();
        info.dateReceived =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
                .getShipmentInfo().getReceivedAt();
        info.dstatus = info.dispatch.getState().getLabel();
        info.waybill =
            info.dispatch.getShipmentInfo() == null ? null : info.dispatch
                .getShipmentInfo().getWaybill();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(DispatchWrapper selected) {
        if (selected == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.dispatch.equals(selected)) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public Dispatch getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.dispatch;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected Boolean canEdit(Dispatch target)
        throws ApplicationException {
        if (target != null) {
            return SessionManager.getAppService().isAllowed(
                new DispatchUpdatePermission(target.getId()));
        }
        return false;
    }

    @Override
    protected Boolean canDelete(Dispatch target)
        throws ApplicationException {
        if (target != null) {
            return SessionManager.getAppService().isAllowed(
                new DispatchDeletePermission(target.getId()));
        }
        return false;
    }

    @Override
    protected Boolean canView(Dispatch target)
        throws ApplicationException {
        if (target != null) {
            return SessionManager.getAppService().isAllowed(
                new DispatchReadPermission(target.getId()));
        }
        return false;
    }

}
