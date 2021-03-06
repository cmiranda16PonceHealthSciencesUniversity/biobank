package edu.ualberta.med.biobank.common.wrappers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.actions.UpdateChildrensTopSpecimenAction;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.loggers.SpecimenLogProvider;
import edu.ualberta.med.biobank.common.wrappers.tasks.NoActionWrapperQueryTask;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.util.RowColPos;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenWrapper extends SpecimenBaseWrapper {
    private static final I18n i18n = I18nFactory.getI18n(SpecimenWrapper.class);

    @SuppressWarnings("nls")
    private static final String DISPATCHS_CACHE_KEY = "dispatchs";

    private static final SpecimenLogProvider LOG_PROVIDER =
        new SpecimenLogProvider();

    private boolean topSpecimenChanged = false;

    public SpecimenWrapper(WritableApplicationService appService,
        Specimen wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected Specimen getNewObject() throws Exception {
        Specimen newObject = super.getNewObject();
        // by default, any newly created Specimen will have a null parent, so
        // its top is itself.
        newObject.setTopSpecimen(newObject);
        return newObject;
    }

    public String getFormattedCreatedAt() {
        return DateFormatter.formatAsDateTime(getCreatedAt());
    }

    public ContainerWrapper getParentContainer() {
        SpecimenPositionWrapper pos = getSpecimenPosition();
        return pos == null ? null : pos.getParent();
    }

    public void setParent(ContainerWrapper container, RowColPos position) {
        if (container == null) {
            setSpecimenPosition(null);
        } else {
            getOrCreatePosition().setParent(container, position);
        }
    }

    public ContainerWrapper getTop() {
        ContainerWrapper top = getParentContainer();
        if (top != null) {
            top = top.getTopContainer();
        }

        return top;
    }

    public boolean hasParent() {
        return getParentContainer() != null;
    }

    public RowColPos getPosition() {
        SpecimenPositionWrapper pos = getSpecimenPosition();
        return pos == null ? null : pos.getPosition();
    }

    private SpecimenPositionWrapper getOrCreatePosition() {
        SpecimenPositionWrapper specimenPosition = getSpecimenPosition();
        if (specimenPosition == null) {
            specimenPosition = new SpecimenPositionWrapper(appService);
            setSpecimenPosition(specimenPosition);
        }
        return specimenPosition;
    }

    public String getPositionString() {
        return getPositionString(true, true);
    }

    @Override
    public SpecimenLogProvider getLogProvider() {
        return LOG_PROVIDER;
    }

    /**
     * Set the position in the given container using the positionString
     */
    @Deprecated
    @SuppressWarnings("nls")
    public void setParentFromPositionString(String positionString,
        ContainerWrapper parentContainer) throws Exception {
        RowColPos rcp = parentContainer.getContainerType()
            .getRowColFromPositionString(
                positionString.replaceFirst(parentContainer.getLabel(), ""));
        if ((rcp.getRow() > -1) && (rcp.getCol() > -1)) {
            setParent(parentContainer, rcp);
        } else {
            throw new Exception(MessageFormat.format("Position \"{0}\" not valid.",
                positionString));
        }
    }

    @SuppressWarnings("nls")
    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        RowColPos position = getPosition();
        if (position == null) {
            return null;
        }

        if (!fullString) {
            return getPositionStringInParent(position, getParentContainer());
        }
        ContainerWrapper directParent = getParentContainer();
        // ContainerPathWrapper path = directParent.getContainerPath();
        String nameShort = directParent.getTopContainer().getContainerType()
            .getNameShort();
        if (addTopParentShortName && nameShort != null)
            return directParent.getLabel()
                + getPositionStringInParent(position, directParent) + " ("
                + nameShort + ")";
        return directParent.getLabel()
            + getPositionStringInParent(position, directParent);
    }

    private String getPositionStringInParent(RowColPos position,
        ContainerWrapper parent) {
        if (parent != null) {
            return parent.getContainerType().getPositionString(position);
        }
        return null;
    }

    public void setQuantityFromType() {
        if (getSpecimenType() == null)
            return;
        CollectionEventWrapper cevent = getCollectionEvent();
        StudyWrapper study = cevent.getPatient().getStudy();
        Collection<AliquotedSpecimenWrapper> aliquotedSpecimenCollection =
            study
                .getAliquotedSpecimenCollection(false);
        for (AliquotedSpecimenWrapper as : aliquotedSpecimenCollection) {
            if (getSpecimenType().equals(as.getSpecimenType())) {
                setQuantity(as.getVolume());
                return;
            }
        }
    }

    @SuppressWarnings("nls")
    private static final String Specimen_QRY = "from "
        + Specimen.class.getName() + " where "
        + SpecimenPeer.INVENTORY_ID.getName() + " = ?";

    /**
     * search in all Specimens list. No matter which site added it.
     */
    @SuppressWarnings("nls")
    public static SpecimenWrapper getSpecimen(
        WritableApplicationService appService, String inventoryId)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria(Specimen_QRY, Arrays.asList(
            new Object[] { inventoryId }));
        List<Specimen> specimens = appService.query(criteria);

        if (specimens == null || specimens.size() == 0) return null;
        if (specimens.size() == 1) return new SpecimenWrapper(appService, specimens.get(0));

        // {0} number of specimens found
        throw new BiobankCheckException(i18n.tr("Error retrieving specimens: found {0} results.",
            specimens.size()));
    }

    @SuppressWarnings("nls")
    private static final String SPECIMENS_NON_ACTIVE_QRY = "from "
        + Specimen.class.getName()
        + " spec where spec."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + " = ? and activityStatus != ?";

    public static List<SpecimenWrapper> getSpecimensNonActiveInCenter(
        WritableApplicationService appService, CenterWrapper<?> center)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(SPECIMENS_NON_ACTIVE_QRY,
            Arrays.asList(new Object[] { center.getId(),
                ActivityStatus.ACTIVE }));
        List<Specimen> Specimens = appService.query(criteria);
        List<SpecimenWrapper> list = new ArrayList<SpecimenWrapper>();

        for (Specimen Specimen : Specimens) {
            list.add(new SpecimenWrapper(appService, Specimen));
        }
        return list;
    }

    @SuppressWarnings("nls")
    public static List<SpecimenWrapper> getSpecimensInSiteWithPositionLabel(
        WritableApplicationService appService, SiteWrapper site, String positionString)
        throws ApplicationException {
        List<ContainerWrapper> possibleContainers = ContainerWrapper.getPossibleParents(
            appService, positionString, site, null);
        List<SpecimenWrapper> Specimens = new ArrayList<SpecimenWrapper>();
        for (ContainerWrapper container : possibleContainers) {
            RowColPos rcp = null;
            try {
                rcp = container.getContainerType().getRowColFromPositionString(
                    positionString.replaceFirst(container.getLabel(), ""));
            } catch (Exception e) {
                // Should never happen: it has been already tested in
                // getPossibleParentsMethod
                assert false;
            }
            if (rcp != null) {
                if ((rcp.getRow() > -1) && (rcp.getCol() > -1)) {
                    SpecimenWrapper Specimen = container.getSpecimen(rcp.getRow(), rcp.getCol());
                    if (Specimen != null) {
                        Specimens.add(Specimen);
                    }
                }
            }
        }
        return Specimens;
    }

    @Override
    public int compareTo(ModelWrapper<Specimen> o) {
        if (o instanceof SpecimenWrapper) {
            String s1 = getPositionString(true, true);
            String s2 = ((SpecimenWrapper) o).getPositionString(true, true);
            if (s1 == null || s2 == null)
                return getInventoryId().compareTo(
                    ((SpecimenWrapper) o).getInventoryId());
            return s1.compareTo(s2);
        }
        return 0;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "inventoryId: " + getInventoryId();
    }

    @SuppressWarnings("unchecked")
    public List<DispatchWrapper> getDispatches() {
        List<DispatchWrapper> dispatchs = (List<DispatchWrapper>) cache.get(DISPATCHS_CACHE_KEY);
        if (dispatchs == null) {
            List<DispatchSpecimenWrapper> dsaList = getDispatchSpecimenCollection();
            if (dsaList != null) {
                dispatchs = new ArrayList<DispatchWrapper>();
                for (DispatchSpecimenWrapper dsa : dsaList) {
                    dispatchs.add(dsa.getDispatch());
                }
                cache.put(DISPATCHS_CACHE_KEY, dispatchs);
            }
        }
        return dispatchs;
    }

    public boolean isActive() {
        ActivityStatus status = getActivityStatus();
        return ActivityStatus.ACTIVE == status;
    }

    public boolean isFlagged() {
        ActivityStatus status = getActivityStatus();
        return ActivityStatus.FLAGGED == status;
    }

    public List<DispatchSpecimenWrapper> getDispatchSpecimenCollection() {
        return getWrapperCollection(SpecimenPeer.DISPATCH_SPECIMENS,
            DispatchSpecimenWrapper.class, false);
    }

    public boolean isUsedInDispatch() {
        return isUsedInDispatch(null);
    }

    public boolean isUsedInDispatch(DispatchWrapper excludedShipment) {
        List<DispatchSpecimenWrapper> dsas = getDispatchSpecimenCollection();
        if (dsas != null)
            for (DispatchSpecimenWrapper dsa : dsas) {
                DispatchWrapper dispatch = dsa.getDispatch();
                if (!dispatch.equals(excludedShipment)
                    && (EnumSet.of(DispatchState.CREATION,
                        DispatchState.IN_TRANSIT, DispatchState.RECEIVED)
                        .contains(dispatch.getDispatchState()))) {
                    if (DispatchSpecimenState.MISSING.equals(dsa
                        .getSpecimenState())) {
                        return false;
                    }
                    return true;
                }
            }
        return false;
    }

    @Override
    public void setParentSpecimen(SpecimenBaseWrapper specimen) {
        super.setParentSpecimen(specimen);

        // keep the top Specimen up-to-date whenever the parent is set; however,
        // only update this top Specimen, not the children's top Specimen. This
        // is so that the children/ descendants will only be updated if the
        // Specimen whose parent Specimen was changed is persisted.
        SpecimenWrapper topSpecimen = specimen == null ? this : specimen
            .getTopSpecimen();
        setTopSpecimenInternal(topSpecimen, true);
    }

    /**
     * Call {@code setParentSpecimen(SpecimenWrapper parent)} instead of this method to change the
     * top {@code Specimen}. The top {@code Specimen} will be automatically updated.
     */
    @SuppressWarnings("nls")
    @Override
    @Deprecated
    public void setTopSpecimen(SpecimenBaseWrapper specimen) {
        throw new UnsupportedOperationException(
            "Not allowed to directly set the top Specimen. Set the parent Specimen instead.");
    }

    protected void setTopSpecimenInternal(SpecimenWrapper specimen,
        boolean checkDatabase) {
        super.setTopSpecimen(specimen);

        if (equals(specimen)) {
            setOriginalCollectionEvent(getCollectionEvent());
        }

        // this is overly cautious, assuming that whenever the top Specimen is
        // set that it is changed. Could be improved to check if the value has
        // actually changed, but would probably require lazy loading.

        if (!isNew() && checkDatabase) {
            // TODO: actually check the database. Get the current
            // topSpecimen through an HQL query and compare it against the
            // one set.
            topSpecimenChanged = true;
            // TODO: may want to set to false if set back to the original?
        } else {
            topSpecimenChanged = true;
        }
    }

    /**
     * Return the top {@code Specimen} of the top loaded {@code Specimen}. This will give the
     * correct "in memory" answer of who the top {@code Specimen} is (whereas super.getTopSpecimen()
     * will give the value from the underlying model).
     */
    @Override
    public SpecimenWrapper getTopSpecimen() {
        // if parent is cached, return their top specimen, otherwise get and
        // return mine (from super).
        if (isPropertyCached(SpecimenPeer.PARENT_SPECIMEN)
            && getParentSpecimen() != null) {
            return getParentSpecimen().getTopSpecimen();
        }
        return super.getTopSpecimen();
    }

    private void addTasksToUpdateChildren(TaskList tasks) {
        if (topSpecimenChanged) {
            SpecimenWrapper topSpecimen = getTopSpecimen();
            if (isPropertyCached(SpecimenPeer.CHILD_SPECIMENS)) {
                // if the children have already been loaded, then update their
                // top specimen so that they update their children, etc. so that
                // the entire subtree is consistent.
                List<SpecimenWrapper> children =
                    getChildSpecimenCollection(false);
                for (SpecimenWrapper child : children) {
                    child.setTopSpecimenInternal(topSpecimen, false);

                    // Save children whether they're are new or not, because the
                    // children's children could be already persistent and need
                    // to be updated (but would then need their parent to be
                    // persisted first).
                    child.addPersistTasks(tasks);
                }
            } else {
                // Use HQL to update all descendants of this Specimen because
                // they are not loaded and loading them would be unnecessary.
                tasks.add(new UpdateChildrensTopSpecimenAction(this));
            }

            tasks.add(new ResetTopSpecimenChangedQueryTask(this));
        }
    }

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.deleteRemovedValue(this, SpecimenPeer.SPECIMEN_POSITION);

        super.addPersistTasks(tasks);

        tasks.persist(this, SpecimenPeer.SPECIMEN_POSITION);

        addTasksToUpdateChildren(tasks);
    }

    @Deprecated
    @Override
    protected void addDeleteTasks(TaskList tasks) {
        // Either Hibernate must delete this object (via the defined cascade) or
        // do it here, but not both. If both are done, then a
        // StaleStateException is thrown because an attempt is made to delete an
        // already deleted object.
        // tasks.delete(this, SpecimenPeer.SPECIMEN_POSITION);

        super.addDeleteTasks(tasks);
    }

    @Override
    protected void resetInternalFields() {
        topSpecimenChanged = false;
    }

    private static class ResetTopSpecimenChangedQueryTask extends
        NoActionWrapperQueryTask<SpecimenWrapper> {
        public ResetTopSpecimenChangedQueryTask(SpecimenWrapper specimen) {
            super(specimen);
        }

        @Override
        public void afterExecute(SDKQueryResult result) {
            getWrapper().topSpecimenChanged = false;
        }
    }

    /**
     * return a string with collection date (different from created at if it is an aliquoted
     * specimen) + the collection center
     */
    @SuppressWarnings("nls")
    public String getCollectionInfo() {
        // {0} when the top specimen was created
        // {1} where the specimen originated from
        // {2} the visit number of the collection event
        return i18n.tr("{0} in {1} (visit #{2})",
            getTopSpecimen().getFormattedCreatedAt(),
            getTopSpecimen().getOriginInfo().getCenter().getNameShort(),
            getCollectionEvent().getVisitNumber());
    }

    public boolean hasUnknownImportType() {
        return getSpecimenType() != null && getSpecimenType().isUnknownImport();
    }

    /**
     * return true if the user can edit this object
     */
}
