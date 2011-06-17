package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;

public class SpecimenTypeInfoTree extends InfoTreeWidget<SpecimenTypeWrapper> {

    private static final String[] HEADINGS = new String[] { Messages.SpecimenTypeInfoTree_name_label,
        Messages.SpecimenTypeInfoTree_nameShort_label };

    public SpecimenTypeInfoTree(Composite parent,
        List<SpecimenTypeWrapper> specimenCollection) {
        super(parent, specimenCollection, HEADINGS, 20);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenTypeWrapper item = null;
                if (element instanceof SpecimenTypeWrapper)
                    item = (SpecimenTypeWrapper) element;
                else
                    item = (SpecimenTypeWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.SpecimenTypeInfoTree_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getNameShort();
                default:
                    return null;
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((SpecimenTypeWrapper) o).toString();
    }

    @Override
    public SpecimenTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        SpecimenTypeWrapper source = (SpecimenTypeWrapper) item.o;
        Assert.isNotNull(source);
        return source;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected List<Node> getNodeChildren(Node node) throws Exception {
        if (node != null && node instanceof BiobankCollectionModel) {
            BiobankCollectionModel model = (BiobankCollectionModel) node;
            Object obj = model.o;
            if (obj != null)
                return createNodes(node,
                    ((SpecimenTypeWrapper) obj)
                        .getChildSpecimenTypeCollection(true));
        }
        return super.getNodeChildren(node);
    }

}