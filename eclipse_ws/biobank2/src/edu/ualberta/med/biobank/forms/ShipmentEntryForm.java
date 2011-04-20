package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenOriginSelectDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ShipmentAdapter shipmentAdapter;

    private OriginInfoWrapper shipment;

    private ComboViewer senderComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SpecimenEntryWidget specimenEntryWidget;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    private static final String DATE_SHIPPED_BINDING = "shipment-date-shipped-binding";

    private DateTimeWidget dateSentWidget;

    private DateTimeWidget dateReceivedWidget;

    private Label departedLabel;

    private NotNullValidator departedValidator;

    private BiobankText waybillWidget;

    private Set<SpecimenWrapper> specimensToPersist = new HashSet<SpecimenWrapper>();

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        if (!shipmentAdapter.getWrapper().isNew())
            shipment = (OriginInfoWrapper) shipmentAdapter.getWrapper()
                .getDatabaseClone();
        else
            shipment = shipmentAdapter.getWrapper();
        try {
            shipment.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipment.isNew()) {
            tabName = "New Shipment";
        } else {
            tabName = "Shipment "
                + shipment.getShipmentInfo().getFormattedDateReceived();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createPatientsSection();
    }

    private void createMainSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        ShipmentInfoWrapper shipInfo = shipment.getShipmentInfo();
        if (shipInfo == null) {
            shipInfo = new ShipmentInfoWrapper(appService);
            shipment.setShipmentInfo(shipInfo);
        }

        senderComboViewer = createComboViewer(client, "Sender",
            ClinicWrapper.getAllClinics(appService),
            (ClinicWrapper) shipment.getCenter(),
            "A sender should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    shipment.setCenter((CenterWrapper<?>) selectedObject);
                    activateWaybillWidget(((ClinicWrapper) selectedObject)
                        .getSendsShipments());
                }
            });
        setFirstControl(senderComboViewer.getControl());

        waybillLabel = widgetCreator.createLabel(client, "Waybill");
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator = new NonEmptyStringValidator(
            "A waybill should be set");
        waybillWidget = (BiobankText) createBoundWidget(client,
            BiobankText.class, SWT.NONE, waybillLabel, new String[0], shipment,
            "shipmentInfo.waybill", waybillValidator, WAYBILL_BINDING);
        activateWaybillWidget(((ClinicWrapper) shipment.getCenter())
            .getSendsShipments());

        shippingMethodComboViewer = createComboViewer(client,
            "Shipping Method",
            ShippingMethodWrapper.getShippingMethods(appService), shipment
                .getShipmentInfo().getShippingMethod(), null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ShippingMethodWrapper method = (ShippingMethodWrapper) selectedObject;
                    ShipmentInfoWrapper shipInfo = shipment.getShipmentInfo();
                    shipInfo.setShippingMethod(method);
                    if (dateSentWidget != null && method != null) {
                        activateDepartedWidget(method.needDate());
                    }
                }
            });

        ShippingMethodWrapper shipMethod = shipInfo.getShippingMethod();
        if (shipInfo.getPackedAt() == null) {
            shipInfo.setPackedAt(new Date());
        }

        departedLabel = widgetCreator.createLabel(client, "Date Sent");
        departedLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        departedValidator = new NotNullValidator("Date Sent should be set");

        dateSentWidget = createDateTimeWidget(client, departedLabel,
            shipInfo.getPackedAt(), shipInfo, "packedAt", departedValidator,
            SWT.DATE | SWT.TIME, DATE_SHIPPED_BINDING);
        activateDepartedWidget(shipMethod != null && shipMethod.needDate());

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Box Number", null, shipInfo, "boxNumber", null);

        if (shipInfo.getReceivedAt() == null) {
            shipInfo.setReceivedAt(new Date());
        }

        dateReceivedWidget = createDateTimeWidget(client, "Date Received",
            shipInfo.getReceivedAt(), shipInfo, "receivedAt",
            new NotNullValidator("Date Received should be set"));
    }

    protected void activateWaybillWidget(boolean waybillNeeded) {
        if (waybillLabel != null && !waybillLabel.isDisposed()) {
            waybillLabel.setVisible(waybillNeeded);
            ((GridData) waybillLabel.getLayoutData()).exclude = !waybillNeeded;
        }
        if (waybillWidget != null && !waybillWidget.isDisposed()) {
            waybillWidget.setVisible(waybillNeeded);
            ((GridData) waybillWidget.getLayoutData()).exclude = !waybillNeeded;

            if (waybillNeeded) {
                widgetCreator.addBinding(WAYBILL_BINDING);
            } else {
                widgetCreator.removeBinding(WAYBILL_BINDING);
                waybillWidget.setText("");
            }
        }
        form.layout(true, true);
    }

    protected void activateDepartedWidget(boolean departedNeeded) {
        dateSentWidget.setVisible(departedNeeded);
        ((GridData) dateSentWidget.getLayoutData()).exclude = !departedNeeded;
        departedLabel.setVisible(departedNeeded);
        ((GridData) departedLabel.getLayoutData()).exclude = !departedNeeded;
        if (departedNeeded) {
            widgetCreator.addBinding(DATE_SHIPPED_BINDING);
        } else {
            widgetCreator.removeBinding(DATE_SHIPPED_BINDING);
        }
        form.layout(true, true);

    }

    private void createPatientsSection() {
        Composite client = createSectionWithClient("Specimens");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens = shipment.getSpecimenCollection(true);

        specimenEntryWidget = new SpecimenEntryWidget(client, SWT.NONE,
            toolkit, appService, true);
        specimenEntryWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(collectionDoubleClickListener);
        specimenEntryWidget.addBinding(widgetCreator,
            "Specimens should be added to a shipment");

        VetoListener<ItemAction, SpecimenWrapper> vetoListener = new VetoListener<ItemAction, SpecimenWrapper>() {
            @Override
            public void handleEvent(Event<ItemAction, SpecimenWrapper> event)
                throws VetoException {
                SpecimenWrapper specimen = event.getObject();
                switch (event.getType()) {
                case PRE_ADD:
                    if (specimen == null)
                        throw new VetoException(
                            "No specimen found for that inventory id.");
                    else if (specimen.isUsedInDispatch())
                        throw new VetoException(
                            "Specimen is currently listed in a dispatch.");
                    else if (specimen.getParentContainer() != null)
                        throw new VetoException(
                            "Specimen is currently listed as stored in a container.");
                    else if (specimen.getOriginInfo() != null
                        && specimen.getOriginInfo().getShipmentInfo() != null
                        && !specimen.getOriginInfo().getShipmentInfo()
                            .equals(this))
                        throw new VetoException(
                            "Specimen is currently part of another shipment: "
                                + specimen.getOriginInfo().getShipmentInfo()
                                + ". You must remove this specimen from that shipment before it can be added to this one.");
                    break;
                case POST_ADD:
                    shipment.addToSpecimenCollection(Arrays.asList(specimen));
                    specimen.setOriginInfo(shipment);
                    break;
                case PRE_DELETE:
                    if (!shipment.isNew()) {
                        try {
                            List<CenterWrapper<?>> centers = CenterWrapper
                                .getCenters(specimen.getAppService());
                            SpecimenOriginSelectDialog dlg = new SpecimenOriginSelectDialog(
                                form.getShell(), specimen, centers);

                            if (dlg.open() == Window.OK) {
                                specimensToPersist.add(specimen);
                            } else {
                                throw new VetoException(
                                    "Must select a new center for this specimen to originate from.");
                            }
                        } catch (ApplicationException e) {
                            throw new VetoException(e.getMessage());
                        }

                    }
                    break;
                case POST_DELETE:
                    shipment.removeFromSpecimenCollection(Arrays
                        .asList(specimen));
                    break;
                }
            }
        };

        specimenEntryWidget.addVetoListener(ItemAction.PRE_ADD, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_ADD, vetoListener);
        specimenEntryWidget
            .addVetoListener(ItemAction.PRE_DELETE, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_DELETE,
            vetoListener);

        specimenEntryWidget.setSpecimens(specimens);
    }

    @Override
    public String getNextOpenedFormID() {
        return ShipmentViewForm.ID;
    }

    @Override
    protected String getOkMessage() {
        return (shipment.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        if (shipment.getShipmentInfo().getWaybill() != null
            && shipment.getShipmentInfo().getWaybill().isEmpty()) {
            shipment.getShipmentInfo().setWaybill(null);
        }

        shipment.persist();

        for (SpecimenWrapper s : specimensToPersist) {
            OriginInfoWrapper origin = s.getOriginInfo();
            origin.persist();

            s.setOriginInfo(origin);
            s.persist();
        }

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SpecimenTransitView.reloadCurrent();
                if (!shipment.getShipmentInfo().isReceivedToday())
                    SpecimenTransitView.showShipment(shipment);
            }
        });
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (shipment.isNew() && senderComboViewer.getCombo().getItemCount() > 1) {
            senderComboViewer.getCombo().deselectAll();
        }
        dateSentWidget.setDate(new Date());
        dateReceivedWidget.setDate(new Date());

        specimenEntryWidget.setSpecimens(shipment.getSpecimenCollection(false));
        ShippingMethodWrapper shipMethod = null;
        if (shipment.getShipmentInfo() != null)
            shipMethod = shipment.getShipmentInfo().getShippingMethod();
        if (shipMethod != null) {
            shippingMethodComboViewer.setSelection(new StructuredSelection(
                shipMethod));
        } else if (shippingMethodComboViewer.getCombo().getItemCount() > 1) {
            shippingMethodComboViewer.getCombo().deselectAll();
        }
    }
}
