package edu.ualberta.med.biobank.dialogs.user;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContextGetAction;
import edu.ualberta.med.biobank.common.action.security.MembershipContextGetInput;
import edu.ualberta.med.biobank.common.action.security.MembershipContextGetOutput;
import edu.ualberta.med.biobank.common.action.security.UserGetOutput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogPage;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcDialogWithPages;
import edu.ualberta.med.biobank.gui.common.widgets.utils.TableFilter;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;

public abstract class UsersPage extends BgcDialogPage {
    private UserInfoTable userInfoTable;

    private final ManagerContext managerContext;

    public UsersPage(BgcDialogWithPages dialog, ManagerContext managerContext) {
        super(dialog);

        this.managerContext = managerContext;
    }

    @Override
    public String getTitle() {
        return Messages.UsersPage_page_title;
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        new TableFilter<User>(content) {
            @Override
            protected boolean accept(User user, String text) {
                return contains(user.getLogin(), text)
                    || contains(user.getEmail(), text)
                    || contains(user.getFullName(), text);
            }

            @Override
            public List<User> getAllCollection() {
                return getCurrentAllUsersList();
            }

            @Override
            public void setFilteredList(List<User> filteredObjects) {
                userInfoTable.setList(filteredObjects);
            }
        };

        userInfoTable =
            new UserInfoTable(content, getCurrentAllUsersList(), managerContext) {
                @Override
                protected int editUser(User user) {
                    int res = super.editUser(user);
                    // when user modify itself. Close everything to log again
                    if (res == UserEditDialog.CLOSE_PARENT_RETURN_CODE) {
                        dialog.close();
                    }
                    return res;
                }

                @Override
                protected boolean deleteUser(User user) {
                    boolean deleted = super.deleteUser(user);
                    if (deleted)
                        getCurrentAllUsersList().remove(user);
                    return deleted;
                }
            };
        setControl(content);
    }

    protected abstract List<User> getCurrentAllUsersList();

    protected void addUser() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                final User user = new User();
                user.setRecvBulkEmails(true);

                MembershipContextGetOutput mcOutput = null;
                try {
                    mcOutput = SessionManager.getAppService()
                        .doAction(new MembershipContextGetAction(
                            new MembershipContextGetInput()));
                } catch (Throwable t) {
                    TmpUtil.displayException(t);
                }

                MembershipContext context = mcOutput.getContext();
                UserGetOutput output = new UserGetOutput(user, context, true);

                UserEditDialog dlg =
                    new UserEditDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        output, managerContext);

                int res = dlg.open();
                if (res == Status.OK) {
                    BgcPlugin.openAsyncInformation(
                        Messages.UserManagementDialog_user_added_title,
                        MessageFormat.format(
                            Messages.UserManagementDialog_user_added_msg,
                            user.getLogin()));

                    getCurrentAllUsersList().add(user);

                    userInfoTable.setList(getCurrentAllUsersList());
                    userInfoTable.setSelection(user);
                }
            }
        });
    }

    @Override
    public void runAddAction() {
        addUser();
    }
}
