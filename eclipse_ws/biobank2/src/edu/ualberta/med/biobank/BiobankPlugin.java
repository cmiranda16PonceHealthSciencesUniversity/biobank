package edu.ualberta.med.biobank;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.p2.BiobankPolicy;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.DateNode;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.ContainerGroup;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeGroup;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteGroup;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.InCreationDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.IncomingNode;
import edu.ualberta.med.biobank.treeview.dispatch.OutgoingNode;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingInTransitDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingNoErrorsDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.ReceivingWithErrorsDispatchGroup;
import edu.ualberta.med.biobank.treeview.dispatch.SentInTransitDispatchGroup;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventGroup;
import edu.ualberta.med.biobank.treeview.request.ApprovedRequestNode;
import edu.ualberta.med.biobank.treeview.request.DispatchCenterAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestSiteAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class BiobankPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "biobank"; //$NON-NLS-1$

    //
    // ContainerTypeAdapter and Container missing on purpose.
    //
    private static Map<String, String> classToImageKey;
    static {
        classToImageKey = new HashMap<String, String>();
        classToImageKey.put(SessionAdapter.class.getName(),
            BgcPlugin.IMG_SESSIONS);
        classToImageKey.put(SiteAdapter.class.getName(), BgcPlugin.IMG_SITE);
        classToImageKey.put(SiteGroup.class.getName(), BgcPlugin.IMG_SITES);
        classToImageKey.put(AbstractClinicGroup.class.getName(),
            BgcPlugin.IMG_CLINICS);
        classToImageKey.put(AbstractStudyGroup.class.getName(),
            BgcPlugin.IMG_STUDIES);
        classToImageKey.put(ContainerTypeGroup.class.getName(),
            BgcPlugin.IMG_CONTAINER_TYPES);
        classToImageKey.put(ContainerGroup.class.getName(),
            BgcPlugin.IMG_CONTAINERS);
        classToImageKey
            .put(ClinicAdapter.class.getName(), BgcPlugin.IMG_CLINIC);
        classToImageKey.put(StudyAdapter.class.getName(), BgcPlugin.IMG_STUDY);
        classToImageKey.put(PatientAdapter.class.getName(),
            BgcPlugin.IMG_PATIENT);
        classToImageKey.put(CollectionEventAdapter.class.getName(),
            BgcPlugin.IMG_PATIENT_VISIT);
        classToImageKey.put(ShipmentAdapter.class.getName(),
            BgcPlugin.IMG_CLINIC_SHIPMENT);
        classToImageKey.put(AbstractSearchedNode.class.getName(),
            BgcPlugin.IMG_SEARCH);
        classToImageKey.put(AbstractTodayNode.class.getName(),
            BgcPlugin.IMG_TODAY);
        classToImageKey.put(DateNode.class.getName(), BgcPlugin.IMG_CALENDAR);
        classToImageKey.put(OutgoingNode.class.getName(), BgcPlugin.IMG_SENT);
        classToImageKey.put(IncomingNode.class.getName(),
            BgcPlugin.IMG_RECEIVED);
        classToImageKey.put(InCreationDispatchGroup.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT_CREATION);
        classToImageKey.put(ReceivingInTransitDispatchGroup.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT_TRANSIT);
        classToImageKey.put(SentInTransitDispatchGroup.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT_TRANSIT);
        classToImageKey.put(ReceivingNoErrorsDispatchGroup.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT_RECEIVING);
        classToImageKey.put(ReceivingWithErrorsDispatchGroup.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT_ERROR);
        classToImageKey.put(DispatchAdapter.class.getName(),
            BgcPlugin.IMG_DISPATCH_SHIPMENT);
        classToImageKey.put(DispatchCenterAdapter.class.getName(),
            BgcPlugin.IMG_SITE);
        classToImageKey.put(RequestSiteAdapter.class.getName(),
            BgcPlugin.IMG_SITE);
        classToImageKey.put(ApprovedRequestNode.class.getName(),
            BgcPlugin.IMG_REQUEST);
        classToImageKey.put(RequestAdapter.class.getName(),
            BgcPlugin.IMG_REQUEST);
        classToImageKey.put(SpecimenAdapter.class.getName(),
            BgcPlugin.IMG_SPECIMEN);
        classToImageKey.put(ProcessingEventAdapter.class.getName(),
            BgcPlugin.IMG_PROCESSING_EVENT);
        classToImageKey.put(ProcessingEventGroup.class.getName(),
            BgcPlugin.IMG_PROCESSING);
    };

    private static final String[] CONTAINER_TYPE_IMAGE_KEYS = new String[] {
        BgcPlugin.IMG_BIN, BgcPlugin.IMG_BOX, BgcPlugin.IMG_CABINET,
        BgcPlugin.IMG_DRAWER, BgcPlugin.IMG_FREEZER, BgcPlugin.IMG_HOTEL,
        BgcPlugin.IMG_PALLET, };

    public static final String BARCODES_FILE = BiobankPlugin.class.getPackage()
        .getName() + ".barcode"; //$NON-NLS-1$

    // The shared instance
    private static BiobankPlugin plugin;

    private ServiceRegistration policyRegistration;

    /**
     * The constructor
     */
    public BiobankPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        SessionManager.getInstance();
        registerP2Policy(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        policyRegistration.unregister();
        policyRegistration = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static BiobankPlugin getDefault() {
        return plugin;
    }

    @Override
    public ImageRegistry getImageRegistry() {
        return BgcPlugin.getDefault().getImageRegistry();
    }

    public boolean windowTitleShowVersionEnabled() {
        return getPreferenceStore().getBoolean(
            PreferenceConstants.GENERAL_SHOW_VERSION);
    }

    public boolean isCancelBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CANCEL).equals(code);
    }

    public boolean isConfirmBarcode(String code) {
        return getPreferenceStore().getString(
            PreferenceConstants.GENERAL_CONFIRM).equals(code);
    }

    public int getPlateNumber(String barcode) {
        return ScannerConfigPlugin.getDefault().getPlateNumber(barcode,
            isRealScanEnabled());
    }

    public static int getPlatesEnabledCount() {
        return ScannerConfigPlugin.getPlatesEnabledCount(isRealScanEnabled());
    }

    public boolean isValidPlateBarcode(String value) {
        return (!value.isEmpty() && (getPlateNumber(value) != -1));
    }

    public static String getActivityLogPath() {
        IPreferenceStore store = getDefault().getPreferenceStore();
        boolean logToFile = store
            .getBoolean(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE);
        if (logToFile) {
            return store
                .getString(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH);
        }
        return null;
    }

    public static boolean isAskPrintActivityLog() {
        IPreferenceStore store = getDefault().getPreferenceStore();
        return store
            .getBoolean(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT);
    }

    public static boolean isRealScanEnabled() {
        String realScan = Platform.getDebugOption(BiobankPlugin.PLUGIN_ID
            + "/realScan"); //$NON-NLS-1$
        if (realScan != null) {
            return Boolean.valueOf(realScan);
        }
        return true;
    }

    public Image getImage(Object object) {
        String imageKey = null;
        if (object == null)
            return null;
        if (object instanceof AdapterBase) {
            Class<?> objectClass = object.getClass();
            while (imageKey == null && !objectClass.equals(AdapterBase.class)) {
                imageKey = classToImageKey.get(objectClass.getName());
                objectClass = objectClass.getSuperclass();
            }
            if ((imageKey == null)
                && ((object instanceof ContainerAdapter) || (object instanceof ContainerTypeAdapter))) {
                String ctName;
                if (object instanceof ContainerAdapter) {
                    ContainerWrapper container = ((ContainerAdapter) object)
                        .getContainer();
                    if (container == null
                        || container.getContainerType() == null)
                        return null;
                    ctName = container.getContainerType().getName();
                } else {
                    ctName = ((ContainerTypeAdapter) object).getLabel();
                }
                return getIconForTypeName(ctName);
            }
        } else {
            if (object instanceof String) {
                imageKey = (String) object;
            }
        }
        return BgcPlugin.getDefault().getImageRegistry().get(imageKey);
    }

    public static ImageDescriptor getImageDescriptor(String key) {
        return BgcPlugin.getDefault().getImageRegistry().getDescriptor(key);
    }

    private Image getIconForTypeName(String typeName) {
        if (typeName == null) {
            return null;
        }
        if (classToImageKey.containsKey(typeName)) {
            return BgcPlugin.getDefault().getImageRegistry()
                .get(classToImageKey.get(typeName));
        }

        String imageKey = null;
        for (String name : CONTAINER_TYPE_IMAGE_KEYS) {
            if (typeName.toLowerCase().contains(name)) {
                imageKey = name;
                break;
            }
        }

        if (imageKey == null)
            imageKey = BgcPlugin.IMG_FREEZER;

        classToImageKey.put(typeName, imageKey);
        return BgcPlugin.getDefault().getImageRegistry().get(imageKey);
    }

    private void registerP2Policy(BundleContext context) {
        policyRegistration = context.registerService(Policy.class.getName(),
            new BiobankPolicy(), null);
    }

    /**
     * Show or hide the heap status based on selection.
     * 
     * @param selection
     */
    public void updateHeapStatus(boolean selection) {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench()
            .getWorkbenchWindows()) {
            if (window instanceof WorkbenchWindow) {
                ((WorkbenchWindow) window).showHeapStatus(selection);
            }
        }
    }

    public static SessionState getSessionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        return (SessionState) service
            .getSourceProvider(SessionState.SESSION_STATE_SOURCE_NAME);
    }

}
