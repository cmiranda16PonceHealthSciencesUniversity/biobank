package edu.ualberta.med.biobank.test.wrappers;

import edu.ualberta.med.biobank.test.TestDatabase;

@Deprecated
public class TestDispatch extends TestDatabase {
    //
    // @Test
    // public void testGettersAndSetters() throws Exception {
    // String name = "testGettersAndSetters" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // testGettersAndSetters(dispatch);
    // }
    //
    // @Test
    // public void testConstructor() throws Exception {
    // Dispatch rawDispatch = new Dispatch();
    // DispatchWrapper dispatch = new DispatchWrapper(appService, rawDispatch);
    // Assert.assertNotNull(dispatch);
    // }
    //
    // @Test
    // public void testGetSetSender() throws Exception {
    // String name = "testGetSetSender" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // DispatchWrapper dispatch = DispatchHelper.newDispatch(null,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // Assert.assertNull(dispatch.getSenderCenter());
    //
    // dispatch.setSenderCenter(senderSite);
    // dispatch.persist();
    // DispatchHelper.createdDispatches.add(dispatch);
    //
    // Assert.assertEquals(senderSite, dispatch.getSenderCenter());
    //
    // DispatchWrapper dispatch2 = new DispatchWrapper(appService,
    // dispatch.getWrappedObject());
    //
    // Assert.assertEquals(senderSite, dispatch2.getSenderCenter());
    // }
    //
    // @Test
    // public void testGetSetReceiver() throws Exception {
    // String name = "testGetSetReceiver" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // DispatchWrapper dispatch = DispatchHelper.newDispatch(senderSite, null,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // Assert.assertNull(dispatch.getReceiverCenter());
    //
    // dispatch.setReceiverCenter(receiverSite);
    // dispatch.persist();
    // DispatchHelper.createdDispatches.add(dispatch);
    //
    // Assert.assertEquals(receiverSite, dispatch.getReceiverCenter());
    //
    // DispatchWrapper dispatch2 = new DispatchWrapper(appService,
    // dispatch.getWrappedObject());
    //
    // Assert.assertEquals(receiverSite, dispatch2.getReceiverCenter());
    // }
    //
    // @Test
    // public void testPersist() throws Exception {
    // String name = "testPersist" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper senderSite2 = SiteHelper.addSite(name + "_sender2");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    // SiteWrapper receiverSite2 = SiteHelper.addSite(name + "_receiver2");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, senderSite2,
    // receiverSite, receiverSite2 };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // ShippingMethodWrapper method = ShippingMethodWrapper
    // .getShippingMethods(appService).get(0);
    // DispatchHelper.addDispatch(senderSite, receiverSite, method, name,
    // Utils.getRandomDate());
    //
    // // test no sender
    // DispatchWrapper dispatch = DispatchHelper.newDispatch(null,
    // receiverSite, method, TestCommon.getNewWaybill(r),
    // Utils.getRandomDate());
    // try {
    // dispatch.persist();
    // dispatch.delete();
    // Assert
    // .fail("should not be allowed to persist a dispatch shipment without a sender");
    // } catch (BiobankSessionException e) {
    // Assert.assertTrue(true);
    // }
    //
    // // test no receiver
    // dispatch = DispatchHelper.newDispatch(senderSite, null, method,
    // TestCommon.getNewWaybill(r), Utils.getRandomDate());
    // try {
    // dispatch.persist();
    // dispatch.delete();
    // Assert
    // .fail("should not be allowed to persist a dispatch shipment without a receiver");
    // } catch (BiobankSessionException e) {
    // Assert.assertTrue(true);
    // }
    //
    // // test duplicate waybill
    //
    // String waybill = TestCommon.getNewWaybill(r);
    // DispatchWrapper dispatch2 = DispatchHelper.newDispatch(senderSite,
    // senderSite, method, waybill, Utils.getRandomDate());
    // dispatch2.persist();
    // DispatchWrapper dispatch3 = DispatchHelper.newDispatch(senderSite,
    // senderSite, method, waybill, Utils.getRandomDate());
    // try {
    // dispatch3.persist();
    // dispatch3.delete();
    // Assert
    // .fail("should not be allowed to persist a dispatch with a used waybill");
    // } catch (BiobankCheckException e) {
    // Assert.assertTrue(true);
    // } finally {
    // dispatch2.delete();
    // }
    // }
    //
    // @Test
    // public void testCompareTo() throws Exception {
    // String name = "testCompareTo" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // ShippingMethodWrapper method = ShippingMethodWrapper
    // .getShippingMethods(appService).get(0);
    //
    // DispatchWrapper dispatch1 = DispatchHelper.addDispatch(senderSite,
    // receiverSite, method);
    // dispatch1.getShipmentInfo().setReceivedAt(
    // DateFormatter.parseToDate("2010-02-01 23:00"));
    //
    // DispatchWrapper dispatch2 = DispatchHelper.addDispatch(senderSite,
    // receiverSite, method);
    // dispatch2.getShipmentInfo().setReceivedAt(
    // DateFormatter.parseToDate("2009-12-01 23:00"));
    //
    // Assert.assertTrue(dispatch1.compareTo(dispatch2) < 0);
    // Assert.assertTrue(dispatch2.compareTo(dispatch1) > 0);
    //
    // }
    //
    // @Test
    // public void testReset() throws Exception {
    // String name = "testReset" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // ShippingMethodWrapper method = ShippingMethodWrapper
    // .getShippingMethods(appService).get(0);
    //
    // // test reset for a new object
    // DispatchWrapper dispatch = DispatchHelper.newDispatch(senderSite,
    // receiverSite, method, name, Utils.getRandomDate());
    //
    // dispatch.reset();
    // Assert.assertEquals(0, dispatch.getCommentCollection(false).size());
    //
    // // test reset for an object already in database
    // dispatch = DispatchHelper.addDispatch(senderSite, receiverSite, method,
    // name, Utils.getRandomDate());
    // // dispatch.addToCommentCollection(Utils.getRandomComments());
    // dispatch.reset();
    // Assert.assertEquals(0, dispatch.getCommentCollection(false).size());
    // }
    //
    // @Test
    // public void testReload() throws Exception {
    // String name = "testReload" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // DispatchWrapper shipment = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0), name, Utils.getRandomDate());
    //
    // try {
    // shipment.reload();
    // Assert.assertTrue(true);
    // } catch (Exception e) {
    // Assert.fail("cannot reload shipment");
    // }
    // }
    //
    // @Test
    // public void testGetWrappedClass() throws Exception {
    // DispatchWrapper dispatch = DispatchHelper.newDispatch(null, null,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // Assert.assertEquals(Dispatch.class, dispatch.getWrappedClass());
    // }
    //
    // @Test
    // public void testDelete() throws Exception {
    // String name = "testDelete" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };
    //
    // for (SiteWrapper site : allSites) {
    // site.persist();
    // site.reload();
    // }
    //
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0), name, Utils.getRandomDate());
    //
    // int countBefore = appService.search(Dispatch.class, new Dispatch())
    // .size();
    //
    // dispatch.delete();
    // DispatchHelper.createdDispatches.remove(dispatch);
    //
    // int countAfter = appService.search(Dispatch.class, new Dispatch())
    // .size();
    //
    // Assert.assertEquals(countBefore - 1, countAfter);
    // }
    //
    // private List<SpecimenWrapper> addSpecimensToContainerRow(
    // ContainerWrapper container, int row,
    // List<SpecimenTypeWrapper> sampleTypes) throws Exception {
    // int numSampletypes = sampleTypes.size();
    // int colCapacity = container.getColCapacity();
    //
    // SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();
    //
    // OriginInfoWrapper oi = new OriginInfoWrapper(appService);
    // oi.setCenter(container.getSite());
    // oi.persist();
    //
    // ProcessingEventWrapper pevent = ProcessingEventHelper
    // .addProcessingEvent(container.getSite(), Utils.getRandomDate());
    //
    // List<SpecimenWrapper> spcs = new ArrayList<SpecimenWrapper>();
    // for (int i = 0; i < colCapacity; ++i) {
    // spcs.add(SpecimenHelper.addSpecimen(parentSpc,
    // sampleTypes.get(r.nextInt(numSampletypes)), pevent, container,
    // row, i));
    // }
    // container.reload();
    // return spcs;
    // }
    //
    // @Test
    // public void testGetSetSpecimenCollection() throws Exception {
    // String name = "testGetSetSpecimenCollection" + r.nextInt();
    // StudyWrapper study = StudyHelper.addStudy(name);
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // List<SpecimenTypeWrapper> sampleTypes = SpecimenTypeWrapper
    // .getAllSpecimenTypes(appService, false);
    // ContainerTypeWrapper containerType = ContainerTypeHelper
    // .addContainerType(senderSite, name, name, 1, 8, 12, false);
    // containerType.addToSpecimenTypeCollection(sampleTypes);
    // containerType.persist();
    // containerType.reload();
    // ContainerTypeWrapper topContainerType = ContainerTypeHelper
    // .addContainerTypeRandom(senderSite, name + "top", true);
    // topContainerType.addToChildContainerTypeCollection(Arrays
    // .asList(containerType));
    // topContainerType.persist();
    // topContainerType.reload();
    // ContainerWrapper topContainer = ContainerHelper.addContainer(
    // String.valueOf(r.nextInt()), name + "top", senderSite,
    // topContainerType);
    // ContainerWrapper container = ContainerHelper.addContainer(null, name,
    // topContainer, senderSite, containerType, 0, 0);
    // PatientHelper.addPatient(name, study);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name);
    // ContactWrapper contact = ContactHelper.addContact(clinic, name);
    // study.addToContactCollection(Arrays.asList(contact));
    // study.persist();
    // study.reload();
    // List<SpecimenWrapper> spcSet1 = addSpecimensToContainerRow(container,
    // 0, sampleTypes);
    // List<SpecimenWrapper> spcSet2 = addSpecimensToContainerRow(container,
    // 1, sampleTypes);
    //
    // dispatch.addSpecimens(spcSet1, DispatchSpecimenState.NONE);
    // dispatch.persist();
    // dispatch.reload();
    //
    // List<SpecimenWrapper> dispatchSpcs = dispatch
    // .getSpecimenCollection(false);
    // Assert.assertEquals(spcSet1.size(), dispatchSpcs.size());
    //
    // // add more specimens to row 2
    //
    // dispatch.addSpecimens(spcSet2, DispatchSpecimenState.NONE);
    // dispatch.persist();
    // dispatch.reload();
    //
    // dispatchSpcs = dispatch.getSpecimenCollection(false);
    // Assert.assertEquals(spcSet1.size() + spcSet2.size(),
    // dispatchSpcs.size());
    //
    // dispatch.removeSpecimens(spcSet1);
    // dispatch.persist();
    // dispatch.reload();
    //
    // dispatchSpcs = dispatch.getSpecimenCollection(false);
    // Assert.assertEquals(spcSet2.size(), dispatchSpcs.size());
    // }
    //
    // @Test
    // public void testRemoveDispatchAliquots() throws Exception {
    // String name = "testRemoveSpecs" + r.nextInt();
    // StudyWrapper study = StudyHelper.addStudy(name);
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // List<SpecimenTypeWrapper> sampleTypes = SpecimenTypeWrapper
    // .getAllSpecimenTypes(appService, false);
    // ContainerTypeWrapper containerType = ContainerTypeHelper
    // .addContainerType(senderSite, name, name, 1, 8, 12, false);
    // containerType.addToSpecimenTypeCollection(sampleTypes);
    // containerType.persist();
    // containerType.reload();
    // ContainerTypeWrapper topContainerType = ContainerTypeHelper
    // .addContainerTypeRandom(senderSite, name + "top", true);
    // topContainerType.addToChildContainerTypeCollection(Arrays
    // .asList(containerType));
    // topContainerType.persist();
    // topContainerType.reload();
    // ContainerWrapper topContainer = ContainerHelper.addContainer(
    // String.valueOf(r.nextInt()), name + "top", senderSite,
    // topContainerType);
    // ContainerWrapper container = ContainerHelper.addContainer(null, name,
    // topContainer, senderSite, containerType, 0, 0);
    // PatientHelper.addPatient(name, study);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name);
    // ContactWrapper contact = ContactHelper.addContact(clinic, name);
    // study.addToContactCollection(Arrays.asList(contact));
    // study.persist();
    // study.reload();
    //
    // List<SpecimenWrapper> spcSet1 = addSpecimensToContainerRow(container,
    // 0, sampleTypes);
    //
    // dispatch.addSpecimens(spcSet1, DispatchSpecimenState.NONE);
    // dispatch.persist();
    // dispatch.reload();
    //
    // List<DispatchSpecimenWrapper> dispatchSpcs = dispatch
    // .getDispatchSpecimenCollection(false);
    // Assert.assertEquals(spcSet1.size(), dispatchSpcs.size());
    //
    // dispatch.removeDispatchSpecimens(dispatchSpcs);
    // dispatch.persist();
    // dispatch.reload();
    //
    // dispatchSpcs = dispatch.getDispatchSpecimenCollection(false);
    // Assert.assertEquals(0, dispatchSpcs.size());
    // }
    //
    // @Test
    // public void testStringOutputs() throws Exception {
    // String name = "testRandomStrings" + r.nextInt();
    // StudyHelper.addStudy(name);
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    // ClinicWrapper senderClinic = ClinicHelper.addClinic("Fake");
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderClinic,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    //
    // Date date = DateFormatter.parseToDate("2011-08-17");
    // ShipmentInfoWrapper info = new ShipmentInfoWrapper(appService);
    // info.setWaybill("testWaybill");
    // info.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
    // appService).get(0));
    // info.setReceivedAt(date);
    // dispatch.setShipmentInfo(info);
    // Assert.assertEquals("2011-08-17 00:00",
    // dispatch.getFormattedReceivedAt());
    // Assert.assertEquals(
    // senderClinic.getNameShort() + "/" + receiverSite.getNameShort()
    // + "/" + dispatch.getFormattedReceivedAt(), dispatch.toString());
    //
    // }
    //
    // @Test
    // public void testRandomGetters() throws Exception {
    // String name = "testRandomGetters" + r.nextInt();
    // StudyWrapper study = StudyHelper.addStudy(name);
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // ClinicWrapper senderClinic = ClinicHelper.addClinic("Fake");
    //
    // SpecimenWrapper spec = SpecimenHelper.addParentSpecimen(senderClinic,
    // study, PatientHelper.addPatient(Utils.getRandomString(10), study));
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderClinic,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // dispatch.addSpecimens(Arrays.asList(spec), DispatchSpecimenState.NONE);
    // Assert.assertTrue(!dispatch.hasSpecimenStatesChanged());
    // Assert.assertTrue(dispatch.hasNewSpecimens());
    //
    // dispatch.setState(DispatchState.CLOSED);
    // Assert.assertTrue(dispatch.isInClosedState());
    // Assert.assertTrue(dispatch.hasBeenReceived());
    //
    // dispatch.setState(DispatchState.CREATION);
    // Assert.assertTrue(dispatch.isInCreationState());
    //
    // dispatch.setState(DispatchState.LOST);
    // Assert.assertTrue(dispatch.isInLostState());
    //
    // dispatch.setState(DispatchState.RECEIVED);
    // Assert.assertTrue(dispatch.isInReceivedState());
    // Assert.assertTrue(dispatch.hasBeenReceived());
    //
    // dispatch.setState(DispatchState.IN_TRANSIT);
    // Assert.assertTrue(dispatch.isInTransitState());
    //
    // dispatch.getDispatchSpecimenCollection(false).get(0)
    // .setDispatchSpecimenState(DispatchSpecimenState.MISSING);
    // Assert.assertTrue(dispatch.hasErrors());
    // Assert.assertTrue(!dispatch.hasSpecimenStatesChanged());
    //
    // Assert.assertTrue(dispatch.hasDispatchSpecimens());
    // dispatch.removeDispatchSpecimens(dispatch
    // .getDispatchSpecimenCollection(false));
    // Assert.assertTrue(!dispatch.hasDispatchSpecimens());
    //
    // UserWrapper currentUser = UserWrapper.getUser(appService, "testuser");
    //
    // Assert.assertTrue(!dispatch.canBeSentBy(currentUser));
    // Assert.assertTrue(!dispatch.canBeReceivedBy(currentUser));
    // Assert.assertTrue(!dispatch.canBeClosedBy(currentUser));
    //
    // }
    //
    // @Test
    // public void testReceiveSpecimens() throws Exception {
    // String name = "testReceiveSpecimens" + r.nextInt();
    // StudyWrapper study = StudyHelper.addStudy(name + "_study");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_site");
    // ClinicWrapper clinic = ClinicHelper.addClinic(name + "_clinic");
    // SpecimenWrapper spec = SpecimenHelper.addParentSpecimen(clinic, study,
    // PatientHelper.addPatient(Utils.getRandomString(10), study));
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(clinic,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    // dispatch.addSpecimens(Arrays.asList(spec), DispatchSpecimenState.NONE);
    //
    // Assert.assertTrue(dispatch.getNonProcessedDispatchSpecimenCollection()
    // .size() == 1);
    // dispatch.receiveSpecimens(Arrays.asList(spec));
    // Assert.assertTrue(dispatch.getReceivedDispatchSpecimens().get(0)
    // .getSpecimen().equals(spec));
    // Assert.assertTrue(dispatch.getNonProcessedDispatchSpecimenCollection()
    // .size() == 0);
    //
    // Assert.assertEquals(0, dispatch.getMissingDispatchSpecimens().size());
    // DispatchSpecimenWrapper dspec = dispatch.getDispatchSpecimen(spec
    // .getInventoryId());
    // dspec.setDispatchSpecimenState(DispatchSpecimenState.MISSING);
    // dspec.persist();
    // dispatch.reload();
    //
    // Assert.assertEquals(1, dispatch.getMissingDispatchSpecimens().size());
    //
    // Assert.assertEquals(1,
    // dispatch.getMap().get(DispatchSpecimenState.MISSING).size());
    //
    // dspec.setDispatchSpecimenState(DispatchSpecimenState.EXTRA);
    // dspec.persist();
    // dispatch.reload();
    //
    // Assert.assertEquals(1, dispatch.getExtraDispatchSpecimens().size());
    //
    // Assert.assertEquals(1,
    // dispatch.getMap().get(DispatchSpecimenState.EXTRA).size());
    //
    // }
    //
    // @Test
    // public void testGetDispatchesByWaybill() throws Exception {
    //
    // Assert.assertTrue(DispatchWrapper.getDispatchesByWaybill(appService,
    // "testWaybill").size() == 0);
    //
    // String name = "testGetDispatchesByWaybill" + r.nextInt();
    // SiteWrapper site = SiteHelper.addSite(name);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name + "_clinic");
    // DispatchWrapper testDispatch = DispatchHelper.addDispatch(site, clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // ShipmentInfoWrapper info = new ShipmentInfoWrapper(appService);
    // info.setWaybill("testWaybill");
    // info.setShippingMethod(ShippingMethodWrapper.getShippingMethods(
    // appService).get(0));
    // testDispatch.setShipmentInfo(info);
    // testDispatch.persist();
    //
    // Assert.assertTrue(DispatchWrapper
    // .getDispatchesByWaybill(appService, "testWaybill").get(0)
    // .equals(testDispatch));
    //
    // }
    //
    // @Test
    // public void testGetDispatchesInSite() throws Exception {
    // String name = "testGetDispatchesInSite" + r.nextInt();
    // SiteWrapper site = SiteHelper.addSite(name);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name + "_clinic");
    // DispatchWrapper testDispatch = DispatchHelper.addDispatch(site, clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // site.reload();
    // clinic.reload();
    // Assert.assertTrue(site.getSrcDispatchCollection(false).size() == 1);
    // Assert.assertTrue(clinic.getDstDispatchCollection(false).size() == 1);
    // testDispatch.setSenderCenter(clinic);
    // testDispatch.setReceiverCenter(site);
    // testDispatch.persist();
    // site.reload();
    // clinic.reload();
    // Assert.assertTrue(clinic.getSrcDispatchCollection(false).size() == 1);
    // Assert.assertTrue(site.getDstDispatchCollection(false).size() == 1);
    // }
    //
    // @Test
    // public void testGetDispatchesInSiteByDateSent() throws Exception {
    // String name = "testGetDispatchesInSiteByDateSent" + r.nextInt();
    // Date date = new Date();
    // SiteWrapper site = SiteHelper.addSite(name);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name + "_clinic");
    // DispatchWrapper testDispatch = DispatchHelper.addDispatch(site, clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // testDispatch.getShipmentInfo().setPackedAt(date);
    // testDispatch.getShipmentInfo().persist();
    // testDispatch.reload();
    // Assert.assertTrue(DispatchWrapper.getDispatchesByDateSent(appService,
    // date, site).size() == 1);
    // Assert.assertTrue(DispatchWrapper.getDispatchesByDateReceived(
    // appService, date, site).size() == 0);
    // testDispatch.setSenderCenter(clinic);
    // testDispatch.setReceiverCenter(site);
    // }
    //
    // @Test
    // public void testGetDispatchesInSiteByDateReceived() throws Exception {
    // String name = "testGetDispatchesInSiteByDateReceived" + r.nextInt();
    // Date date = new Date();
    // SiteWrapper site = SiteHelper.addSite(name);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name + "_clinic");
    // DispatchWrapper testDispatch = DispatchHelper.addDispatch(site, clinic,
    // ShippingMethodWrapper.getShippingMethods(appService).get(0));
    // testDispatch.getShipmentInfo().setReceivedAt(date);
    // testDispatch.getShipmentInfo().persist();
    // testDispatch.reload();
    // Assert.assertTrue(DispatchWrapper.getDispatchesByDateReceived(
    // appService, date, site).size() == 1);
    // Assert.assertTrue(DispatchWrapper.getDispatchesByDateSent(appService,
    // date, site).size() == 0);
    // testDispatch.setSenderCenter(clinic);
    // testDispatch.setReceiverCenter(site);
    // }
    //
    // @Test
    // public void testSwitchToTransit() throws Exception {
    // // expect specimen with position to have their position removed.
    // String name = "testSwitchToTransit" + r.nextInt();
    // StudyWrapper study = StudyHelper.addStudy(name);
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
    // receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
    // .get(0));
    //
    // // create specimens with position.
    // List<SpecimenTypeWrapper> specTypes = SpecimenTypeWrapper
    // .getAllSpecimenTypes(appService, false);
    // ContainerTypeWrapper containerType = ContainerTypeHelper
    // .addContainerType(senderSite, name, name, 1, 8, 12, false);
    // containerType.addToSpecimenTypeCollection(specTypes);
    // containerType.persist();
    // containerType.reload();
    // ContainerTypeWrapper topContainerType = ContainerTypeHelper
    // .addContainerTypeRandom(senderSite, name + "top", true);
    // topContainerType.addToChildContainerTypeCollection(Arrays
    // .asList(containerType));
    // topContainerType.persist();
    // topContainerType.reload();
    // ContainerWrapper topContainer = ContainerHelper.addContainer(
    // String.valueOf(r.nextInt()), name + "top", senderSite,
    // topContainerType);
    // ContainerWrapper container = ContainerHelper.addContainer(null, name,
    // topContainer, senderSite, containerType, 0, 0);
    //
    // PatientHelper.addPatient(name, study);
    // ClinicWrapper clinic = ClinicHelper.addClinic(name);
    // ContactWrapper contact = ContactHelper.addContact(clinic, name);
    // study.addToContactCollection(Arrays.asList(contact));
    // study.persist();
    // study.reload();
    //
    // List<SpecimenWrapper> spcSet1 = addSpecimensToContainerRow(container,
    // 0, specTypes);
    //
    // dispatch.addSpecimens(spcSet1, DispatchSpecimenState.NONE);
    // dispatch.persist();
    // dispatch.reload();
    //
    // for (SpecimenWrapper sp : spcSet1) {
    // sp.reload();
    // Assert.assertNotNull(sp.getPosition());
    // }
    //
    // dispatch.setState(DispatchState.IN_TRANSIT);
    // dispatch.persist();
    //
    // for (SpecimenWrapper sp : spcSet1) {
    // sp.reload();
    // Assert.assertNull(sp.getPosition());
    // }
    // }
    //
    // @Test
    // public void testDispatchSpecimenCompareTo() {
    // SpecimenWrapper s1 = new SpecimenWrapper(appService);
    // s1.setInventoryId("a");
    //
    // SpecimenWrapper s2 = new SpecimenWrapper(appService);
    // s2.setInventoryId("b");
    //
    // DispatchSpecimenWrapper ds1 = new DispatchSpecimenWrapper(appService);
    // ds1.setSpecimen(s1);
    //
    // DispatchSpecimenWrapper ds2 = new DispatchSpecimenWrapper(appService);
    // ds2.setSpecimen(s1);
    //
    // Assert.assertTrue(ds1.compareTo(ds2) == 0);
    //
    // ds2.setSpecimen(s2);
    // Assert.assertTrue(ds1.compareTo(ds2) < 0);
    //
    // DispatchSpecimenWrapper2 ds3 = new DispatchSpecimenWrapper2(appService);
    //
    // Assert.assertTrue(ds1.compareTo(ds3) == 0);
    // }
    //
    // private static final class DispatchSpecimenWrapper2 extends
    // ModelWrapper<DispatchSpecimen> {
    //
    // public DispatchSpecimenWrapper2(WritableApplicationService appService) {
    // super(appService);
    // }
    //
    // @Override
    // public Property<Integer, ? super DispatchSpecimen> getIdProperty() {
    // return DispatchSpecimenPeer.ID;
    // }
    //
    // @Override
    // protected List<Property<?, ? super DispatchSpecimen>> getProperties() {
    // return new ArrayList<Property<?, ? super DispatchSpecimen>>();
    // }
    //
    // @Override
    // public Class<DispatchSpecimen> getWrappedClass() {
    // return DispatchSpecimen.class;
    // }
    // }
    //
    // @Test
    // public void testDispatchSpecimenState() throws Exception {
    // DispatchSpecimenWrapper ds = new DispatchSpecimenWrapper(appService);
    //
    // DispatchSpecimenState[] states = DispatchSpecimenState.values();
    // for (DispatchSpecimenState state : states) {
    // ds.setState(state);
    // Assert
    // .assertTrue(ds.getStateDescription().equals(state.getLabel()));
    // Assert.assertTrue(ds.getSpecimenState().equals(state));
    // }
    // }
    //
    // /**
    // * Related to issue #1438: should insert with no problem the dispatch if
    // no
    // * shipmentinfo is set + can insert more than one dispatch with an empty
    // * waybill, with the same sender.
    // */
    // @Test
    // public void testPersistWaybillEmpty() throws Exception {
    // String name = "testPersistWaybillUnique" + r.nextInt();
    // SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
    // SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
    //
    // ShippingMethodWrapper method = ShippingMethodWrapper
    // .getShippingMethods(appService).get(0);
    // DispatchWrapper dispatch1 = DispatchHelper.newDispatch(senderSite,
    // receiverSite, method, "", Utils.getRandomDate());
    // dispatch1.persist();
    // DispatchHelper.createdDispatches.add(dispatch1);
    //
    // // Test with no shipment info:
    // DispatchWrapper dispatch2 = DispatchHelper.newDispatch(senderSite,
    // receiverSite, method, "", Utils.getRandomDate());
    // dispatch2.setShipmentInfo(null);
    // try {
    // dispatch2.persist();
    // DispatchHelper.createdDispatches.add(dispatch2);
    // } catch (BiobankCheckException e) {
    // Assert.fail("Persist should succeed");
    // }
    // Assert.assertNull(dispatch2.getShipmentInfo());
    //
    // // Test with another dispatch with waybill empty
    // DispatchWrapper dispatch3 = DispatchHelper.newDispatch(senderSite,
    // receiverSite, method, "", Utils.getRandomDate());
    // try {
    // dispatch3.persist();
    // DispatchHelper.createdDispatches.add(dispatch3);
    // } catch (BiobankCheckException e) {
    // Assert.fail("Persist should succeed");
    // }
    // Assert.assertEquals("", dispatch3.getShipmentInfo().getWaybill());
    // Assert.assertEquals(dispatch1.getShipmentInfo().getWaybill(), dispatch3
    // .getShipmentInfo().getWaybill());
    //
    // }
}
