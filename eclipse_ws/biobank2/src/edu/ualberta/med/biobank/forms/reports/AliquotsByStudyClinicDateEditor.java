package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.DateGroup;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;

public class AliquotsByStudyClinicDateEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByStudyClinicDateEditor";
    private ComboViewer dateRangeCombo;
    protected DateTimeWidget start;
    protected DateTimeWidget end;
    protected TopContainerListWidget topContainers;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100 };
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {
            "Study",
            "Clinic",
            ((IStructuredSelection) dateRangeCombo.getSelection())
                .getFirstElement().toString(), "Total" };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        dateRangeCombo = widgetCreator.createComboViewer(parent, "Group By",
            Arrays.asList(DateGroup.values()), null);
        dateRangeCombo.getCombo().select(0);
        widgetCreator.createLabel(parent, "Top Containers");
        topContainers = new TopContainerListWidget(parent, SWT.NONE);
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null, SWT.DATE);

    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(((IStructuredSelection) dateRangeCombo.getSelection())
            .getFirstElement().toString());
        params.add(topContainers.getSelectedContainers());
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Group By");
        param.add("Top Container");
        param.add("Start Date (Linked)");
        param.add("End Date (Linked)");
        return param;
    }

}