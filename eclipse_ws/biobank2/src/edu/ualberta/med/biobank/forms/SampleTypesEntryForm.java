package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.SampleTypeEntryWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

public class SampleTypesEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";
    public static final String OK_MESSAGE = "View and edit sample types.";

    private List<SampleTypeWrapper> globalSampleTypes;
    private List<SampleTypeWrapper> siteSampleTypes;
    private SiteWrapper siteWrapper;
    private SampleTypeEntryWidget siteSampleWidget;
    private SampleTypeEntryWidget globalSampleWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        SiteAdapter siteAdapter = (SiteAdapter) adapter;
        siteWrapper = siteAdapter.getWrapper();
        globalSampleTypes = SampleTypeWrapper.getAllWrappers(appService, true);
        siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
        setPartName("Sample Types Entry");
    }

    @Override
    protected void createFormContent() {
        form.setText("Sample Type Information");
        form.getBody().setLayout(new GridLayout(1, false));
        createSiteSampleTypeSection();
        createGlobalSampleTypeSection();
    }

    private void createSiteSampleTypeSection() {
        Composite client = createSectionWithClient("Site Sample Types");
        GridLayout layout = new GridLayout(1, true);
        client.setLayout(layout);

        siteSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
            siteSampleTypes, globalSampleTypes, "Add Site Sample Type", toolkit);
        siteSampleWidget.adaptToToolkit(toolkit, true);
        siteSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(siteSampleWidget);
        firstControl = siteSampleWidget;
    }

    private void createGlobalSampleTypeSection() {
        Composite client = createSectionWithClient("Global Sample Types");
        GridLayout layout = new GridLayout(1, true);
        client.setLayout(layout);

        globalSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
            globalSampleTypes, siteSampleTypes, "Add Global Sample Type",
            toolkit);
        globalSampleWidget.adaptToToolkit(toolkit, true);
        globalSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalSampleWidget);
    }

    @Override
    public void saveForm() throws Exception {
        List<SampleTypeWrapper> ssCollection = siteSampleWidget
            .getTableSampleTypes();
        siteWrapper.setSampleTypeCollection(ssCollection);

        ssCollection = globalSampleWidget.getTableSampleTypes();
        for (SampleTypeWrapper ss : ssCollection) {
            ss.persist();
        }
        SampleTypeWrapper.deleteOldSampleTypes(ssCollection, globalSampleTypes);
    }

    @Override
    public String getNextOpenedFormID() {
        return null;
    }

    @Override
    protected String getOkMessage() {
        return null;
    }

}
