package edu.ualberta.med.biobank.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteUtils {

    private static final Logger logger = Logger.getLogger(SiteUtils.class
        .getName());

    public static Study getStudyInSite(WritableApplicationService appService,
        String nameShort, Site site) {
        HQLCriteria criteria = new HQLCriteria("from " + Study.class.getName()
            + " where nameShort = ? and site = ?", Arrays.asList(new Object[] {
            nameShort, site }));
        List<Study> studies;
        try {
            studies = appService.query(criteria);
            if (studies.size() == 1) {
                return studies.get(0);
            }
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a study", e);
        }
        return null;
    }

    public static List<Sample> getSamplesInSite(
        WritableApplicationService appService, String inventoryId, Site site) {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " where inventoryId = ? and patientVisit.patient.study.site = ?",
            Arrays.asList(new Object[] { inventoryId, site }));
        try {
            return appService.query(criteria);
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a sample", e);
        }
        return null;
    }

    /**
     * Get containers types defined in a site. if useStrictName is true, then
     * the container type name should be exactly containerName, otherwise, it
     * will contains containerName.
     */
    public static List<ContainerType> getContainerTypesInSite(
        WritableApplicationService appService, Site site, String containerName,
        boolean useStrictName) throws ApplicationException {
        String nameComparison = "=";
        String containerNameParameter = containerName;
        if (!useStrictName) {
            nameComparison = "like";
            containerNameParameter = "%" + containerName + "%";
        }
        String query = "from " + ContainerType.class.getName()
            + " where site = ? and name " + nameComparison + " ?";
        HQLCriteria criteria = new HQLCriteria(query, Arrays
            .asList(new Object[] { site, containerNameParameter }));
        return appService.query(criteria);
    }

    public static Collection<ContainerType> getTopContainerTypesInSite(
        WritableApplicationService appService, Site site) {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName()
            + " where site = ? and topLevel=true", Arrays
            .asList(new Object[] { site }));
        try {
            return appService.query(criteria);
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a containers", e);
        }
        return Collections.emptyList();
    }

}
