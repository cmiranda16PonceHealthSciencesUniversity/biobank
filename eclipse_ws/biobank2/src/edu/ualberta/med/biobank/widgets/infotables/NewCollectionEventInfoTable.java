package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.cevent.CollectionEventInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class NewCollectionEventInfoTable extends InfoTableWidget {

    private static final String[] HEADINGS = new String[] {
        Messages.CollectionEventInfoTable_header_visitNumber,
        Messages.CollectionEventInfoTable_header_numSourceSpecimens,
        Messages.CollectionEventInfoTable_header_numAliquotedSpecimens,
        Messages.CollectionEventInfoTable_header_comment };

    public NewCollectionEventInfoTable(Composite parent,
        List<CollectionEventInfo> collection) {
        super(parent, collection, HEADINGS, 10, CollectionEvent.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                CollectionEventInfo info = (CollectionEventInfo) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.cevent.getVisitNumber().toString();
                case 1:
                    return NumberFormatter.format(info.sourceSpecimenCount);
                case 2:
                    return NumberFormatter.format(info.aliquotedSpecimenCount);
                case 3:
                    return info.cevent.getComment();

                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((CollectionEventInfo) o).toString();
    }

    @Override
    public CollectionEvent getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        CollectionEventInfo row = (CollectionEventInfo) item.o;
        Assert.isNotNull(row);
        return row.cevent;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}