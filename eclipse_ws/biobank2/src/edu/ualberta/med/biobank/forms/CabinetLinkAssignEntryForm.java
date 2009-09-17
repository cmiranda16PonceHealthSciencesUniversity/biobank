package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CabinetLinkAssignEntryForm extends AbstractPatientAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm";

    private Patient currentPatient;

    private Label cabinetLabel;
    private Label drawerLabel;
    private ViewContainerWidget cabinetWidget;
    private CabinetDrawerWidget drawerWidget;

    private Text patientNumberText;
    private CCombo comboVisits;
    private ComboViewer viewerVisits;
    private ComboViewer comboViewerSampleTypes;
    private Text inventoryIdText;
    private Text positionText;
    private Button checkPositionButton;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue patientNumberValue = new WritableValue("",
        String.class);
    private IObservableValue visitSelectionValue = new WritableValue("",
        String.class);
    private IObservableValue positionValue = new WritableValue("", String.class);
    private IObservableValue resultShownValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue selectedSampleTypeValue = new WritableValue("",
        String.class);

    private SampleWrapper sampleWrapper;
    private Container cabinet;
    private Container drawer;
    private Container bin;

    private static final String CHECK_CLICK_MESSAGE = "Click on check";

    @Override
    protected void init() {
        setPartName("Cabinet Link/Assign");
        sampleWrapper = new SampleWrapper(appService, new Sample());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Link and Assign Cabinet Samples");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();
        createLocationSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShownValue, CHECK_CLICK_MESSAGE);
    }

    private void createLocationSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);
        toolkit.paintBordersFor(client);

        cabinetLabel = toolkit.createLabel(client, "Cabinet");
        drawerLabel = toolkit.createLabel(client, "Drawer");

        cabinetWidget = new ViewContainerWidget(client);
        toolkit.adapt(cabinetWidget);
        cabinetWidget.setGridSizes(4, 1, 150, 150);
        cabinetWidget.setFirstColSign('A');
        cabinetWidget.setShowColumnFirst(true);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        cabinetWidget.setLayoutData(gdDrawer);

        drawerWidget = new CabinetDrawerWidget(client);
        toolkit.adapt(drawerWidget);
        GridData gdBin = new GridData();
        gdBin.widthHint = CabinetDrawerWidget.WIDTH;
        gdBin.heightHint = CabinetDrawerWidget.HEIGHT;
        gdBin.verticalSpan = 2;
        drawerWidget.setLayoutData(gdBin);

    }

    private void createFieldsSection() {
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Patient Number", new String[0],
            patientNumberValue, NonEmptyString.class, "Enter a patient number");
        patientNumberText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                setVisitsList();
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setVisitsList();
            }
        });
        createVisitCombo(fieldsComposite);

        inventoryIdText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Inventory ID", new String[0],
            PojoObservables.observeValue(sampleWrapper, "inventoryId"),
            NonEmptyString.class, "Enter Inventory Id");
        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        positionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Position", new String[0], positionValue,
            NonEmptyString.class, "Enter a position (eg 01AA01AB)");
        positionText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (checkPositionButton.isEnabled()) {
                    checkPositionAndSample();
                }
            }
        });

        createTypeCombo(fieldsComposite);

        checkPositionButton = toolkit.createButton(fieldsComposite, "Check",
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        checkPositionButton.setLayoutData(gd);
        checkPositionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkPositionAndSample();
            }
        });

    }

    private void createTypeCombo(Composite fieldsComposite) {
        List<SampleType> sampleTypes;
        try {
            sampleTypes = SampleTypeWrapper.getSampleTypeNotInPalletsOrBoxes(
                appService, SessionManager.getInstance().getCurrentSite());
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Initialisation failed", e);
            sampleTypes = new ArrayList<SampleType>();
        }
        comboViewerSampleTypes = createCComboViewerWithNoSelectionValidator(
            fieldsComposite, "Sample type", sampleTypes, null,
            "A sample type should be selected");
        comboViewerSampleTypes
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) comboViewerSampleTypes
                        .getSelection();
                    sampleWrapper.setSampleType((SampleType) stSelection
                        .getFirstElement());
                }
            });
        if (sampleTypes.size() == 1) {
            comboViewerSampleTypes.getCCombo().select(0);
            sampleWrapper.setSampleType(sampleTypes.get(0));
        }
    }

    private void createVisitCombo(Composite client) {
        comboVisits = (CCombo) createBoundWidgetWithLabel(client, CCombo.class,
            SWT.READ_ONLY | SWT.BORDER | SWT.FLAT, "Visits", new String[0],
            visitSelectionValue, NonEmptyString.class,
            "A visit should be selected");
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        comboVisits.setLayoutData(gridData);

        viewerVisits = new ComboViewer(comboVisits);
        viewerVisits.setContentProvider(new ArrayContentProvider());
        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisit pv = (PatientVisit) element;
                return BioBankPlugin.getDateTimeFormatter().format(
                    pv.getDateDrawn());
            }
        });
        comboVisits.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    inventoryIdText.setFocus();
                }
            }
        });
        comboVisits.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sampleWrapper.setPatientVisit(getSelectedPatientVisit());
            }
        });
    }

    protected void setVisitsList() {
        String pNumber = patientNumberText.getText();
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatientInSite(appService,
                pNumber, SessionManager.getInstance().getCurrentSite());
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Error getting the patient", e);
        }
        if (currentPatient != null) {
            // show visits list
            Collection<PatientVisit> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            comboVisits.select(0);
            comboVisits.setListVisible(true);
        }
    }

    protected void checkPositionAndSample() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    DatabaseResult res = sampleWrapper.checkInventoryIdUnique();
                    if (res != DatabaseResult.OK) {
                        BioBankPlugin.openError("Check position and sample",
                            res.getMessage());
                        resultShownValue.setValue(Boolean.FALSE);
                        return;
                    }
                    String positionString = positionText.getText();
                    initParentContainersFromPosition(positionString);
                    if (bin == null) {
                        resultShownValue.setValue(Boolean.FALSE);
                        return;
                    }

                    sampleWrapper.setSamplePositionFromString(positionString,
                        bin);
                    res = sampleWrapper.checkPosition(bin);
                    if (res != DatabaseResult.OK) {
                        BioBankPlugin.openError("Check position and sample",
                            res.getMessage());
                        resultShownValue.setValue(Boolean.FALSE);
                        return;
                    }
                    sampleWrapper.getSamplePosition().setContainer(bin);

                    showPositions();

                    resultShownValue.setValue(Boolean.TRUE);
                    cancelConfirmWidget.setFocus();
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    BioBankPlugin.openError("Error while checking position", e);
                }
                setDirty(true);
            }

        });
    }

    private void showPositions() {
        Point drawerPosition = new Point(drawer.getPosition().getRow(), drawer
            .getPosition().getCol());
        cabinetWidget.setSelectedBox(drawerPosition);
        cabinetLabel.setText("Cabinet " + cabinet.getLabel());
        drawerWidget.setSelectedBin(bin.getPosition().getRow());
        drawerLabel.setText("Drawer " + drawer.getLabel());
        form.layout(true, true);
    }

    protected void initParentContainersFromPosition(String positionString)
        throws Exception {
        String binLabel = positionString.substring(0, 6);

        List<Container> containers = ContainerWrapper
            .getContainersHoldingSampleType(appService, SessionManager
                .getInstance().getCurrentSite(), binLabel, sampleWrapper
                .getSampleType());
        if (containers.size() == 1) {
            bin = containers.get(0);
            drawer = bin.getPosition().getParentContainer();
            cabinet = drawer.getPosition().getParentContainer();
        } else if (containers.size() == 0) {
            containers = ContainerWrapper.getContainersWithLabelInSite(
                appService, SessionManager.getInstance().getCurrentSite(),
                binLabel);
            if (containers.size() > 0) {
                BioBankPlugin.openError("Check position and sample",
                    "Bin labelled " + binLabel
                        + " cannont hold samples of type "
                        + sampleWrapper.getSampleType().getName());
            } else {
                BioBankPlugin.openError("Check position and sample",
                    "Can't find bin labelled " + binLabel);
            }
            return;
        } else {
            throw new Exception("should do something");
        }
    }

    @Override
    public void cancelForm() {
        sampleWrapper.setWrappedObject(new Sample());
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelectedBox(null);
        drawerWidget.setSelectedBin(0);
        resultShownValue.setValue(Boolean.FALSE);
        selectedSampleTypeValue.setValue("");
        inventoryIdText.setText("");
        positionText.setText("");
    }

    @Override
    protected void saveForm() throws Exception {
        sampleWrapper.setLinkDate(new Date());
        DatabaseResult res = sampleWrapper.persist();
        if (res != DatabaseResult.OK) {
            BioBankPlugin.openError("Cabinet sample save", res.getMessage());
            return;
        }
        setSaved(true);
    }

    private PatientVisit getSelectedPatientVisit() {
        if (viewerVisits.getSelection() != null
            && viewerVisits.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewerVisits
                .getSelection();
            if (selection.size() == 1)
                return (PatientVisit) selection.getFirstElement();
        }
        return null;
    }

    @Override
    protected String getOkMessage() {
        return "Add cabinet samples.";
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            checkPositionButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            if (status.getMessage() != null
                && status.getMessage().contentEquals(CHECK_CLICK_MESSAGE)) {
                checkPositionButton.setEnabled(true);
            } else {
                checkPositionButton.setEnabled(false);
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected void print() {
        // FIXME implement print functionnality
        System.out.println("PRINT activity");
    }

}
