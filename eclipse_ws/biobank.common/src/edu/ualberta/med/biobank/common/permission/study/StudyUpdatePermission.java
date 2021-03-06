package edu.ualberta.med.biobank.common.permission.study;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

public class StudyUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    public StudyUpdatePermission(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyUpdatePermission() {

    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // get is intended, null value indicates any study
        Study study = context.get(Study.class, studyId);
        return PermissionEnum.STUDY_UPDATE.isAllowed(context.getUser(), study);
    }
}
