package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;
import java.util.Map;

/**
 * Application service interface obtained through
 * "ApplicationServiceProvider.getApplicationServiceFromUrl" method. This
 * replace the default WritableApplicationService interface to add our own
 * methods.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public interface BiobankApplicationService extends WritableApplicationService {

    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException;

    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception;

    public void logActivity(Log log) throws Exception;

    public void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException;

    public List<Group> getSecurityGroups(User currentUser,
        boolean includeSuperAdmin) throws ApplicationException;

    public List<User> getSecurityUsers(User currentUser)
        throws ApplicationException;

    public User persistUser(User currentUser, User userToPersist)
        throws ApplicationException;

    public void deleteUser(User currentUser, String loginToDelete)
        throws ApplicationException;

    public User getCurrentUser() throws ApplicationException;

    public Group persistGroup(Group group) throws ApplicationException;

    public void deleteGroup(Group group) throws ApplicationException;

    public void unlockUser(User currentUser, String userNameToUnlock)
        throws ApplicationException;

    public List<Object> runReport(Report report, int maxResults, int firstRow,
        int timeout) throws ApplicationException;

    public void checkVersion(String clientVersion) throws ApplicationException;

    public String getServerVersion();

    public List<ProtectionGroupPrivilege> getSecurityGlobalFeatures(
        User currentUser) throws ApplicationException;

    public List<ProtectionGroupPrivilege> getSecurityCenterFeatures(
        User currentUser) throws ApplicationException;

    public QueryHandle createQuery(QueryCommand qc) throws Exception;

    public List<Object> startQuery(QueryHandle qh) throws Exception;

    public void stopQuery(QueryHandle qh) throws Exception;

    public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
        ProcessData processData, boolean rescanMode, User user)
        throws ApplicationException;

    public CellProcessResult processCellStatus(Cell cell,
        ProcessData processData, User user) throws ApplicationException;

    public List<String> executeGetSourceSpecimenUniqueInventoryIds(int numIds)
        throws ApplicationException;

    public String uploadFile(byte[] bytes, String uploadFile)
        throws ApplicationException;

}
