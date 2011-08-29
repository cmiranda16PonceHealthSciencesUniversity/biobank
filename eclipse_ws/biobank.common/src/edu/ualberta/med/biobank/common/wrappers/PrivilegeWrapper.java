package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.wrappers.base.PrivilegeBaseWrapper;
import edu.ualberta.med.biobank.model.Privilege;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PrivilegeWrapper extends PrivilegeBaseWrapper {

    /**
     * This class is like an enum, there is no user modification of its content,
     * so we can use a cache.
     */
    public static Map<String, PrivilegeWrapper> privileges;

    public PrivilegeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PrivilegeWrapper(WritableApplicationService appService,
        Privilege wrappedObject) {
        super(appService, wrappedObject);
    }

    private static final String PRIVILEGE_QRY = "from "
        + Privilege.class.getName() + " where name = ?";

    public static PrivilegeWrapper getPrivilege(
        WritableApplicationService appService, String name)
        throws ApplicationException, BiobankFailedQueryException {

        HQLCriteria c = new HQLCriteria(PRIVILEGE_QRY,
            Arrays.asList(new Object[] { name }));

        List<Privilege> result = appService.query(c);
        if (result.size() != 1)
            throw new BiobankFailedQueryException(
                "unexpected results from query");
        return new PrivilegeWrapper(appService, result.get(0));
    }

    private static final String ALL_PRIVILEGES_QRY = "from "
        + Privilege.class.getName();

    private static synchronized Map<String, PrivilegeWrapper> getPrivileges(
        WritableApplicationService appService) throws ApplicationException {
        if (privileges == null) {
            privileges = new HashMap<String, PrivilegeWrapper>();
            HQLCriteria criteria = new HQLCriteria(ALL_PRIVILEGES_QRY,
                new ArrayList<Object>());
            List<Privilege> pList = appService.query(criteria);
            for (Privilege p : pList) {
                privileges
                    .put(p.getName(), new PrivilegeWrapper(appService, p));
            }
        }
        return privileges;
    }

    public static PrivilegeWrapper getReadPrivilege(
        WritableApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Read"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getUpdatePrivilege(
        WritableApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Update"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getDeletePrivilege(
        WritableApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Delete"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getCreatePrivilege(
        WritableApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Create"); //$NON-NLS-1$
    }

    public static List<PrivilegeWrapper> getAllPrivileges(
        WritableApplicationService appService) throws ApplicationException {
        return (List<PrivilegeWrapper>) getPrivileges(appService).values();
    }
}
