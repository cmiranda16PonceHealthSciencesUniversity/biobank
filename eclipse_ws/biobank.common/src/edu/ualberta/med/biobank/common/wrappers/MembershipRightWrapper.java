package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipRightBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRight;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipRightWrapper extends MembershipRightBaseWrapper {

    public MembershipRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRightWrapper(WritableApplicationService appService,
        MembershipRight m) {
        super(appService, m);
    }

    @Override
    public String getMembershipObjectsListString() {
        // TODO Auto-generated method stub
        return "";
    }

    /**
     * Don't use HQL because this user method will be call a lot. It is better
     * to call getter, they will be loaded once and the kept in memory
     */
    @Override
    public List<PrivilegeWrapper> getPrivilegesForRight(BbRightWrapper right)
        throws ApplicationException {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
            if (rp.getRight().equals(right))
                privileges.addAll(rp.getPrivilegeCollection(false));
        }
        return privileges;
    }
}
