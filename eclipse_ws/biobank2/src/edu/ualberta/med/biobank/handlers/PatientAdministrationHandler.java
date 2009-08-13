package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.rcp.PatientsAdministrationPerspective;

public class PatientAdministrationHandler extends AbstractHandler implements
    IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // open the perspective
        try {
            IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
            workbench.showPerspective(PatientsAdministrationPerspective.ID,
                workbench.getActiveWorkbenchWindow());
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                .getActivePage();
            page.resetPerspective();

            for (IEditorReference ref : page.getEditorReferences()) {
                IEditorPart part = ref.getEditor(false);
                if (part != null) {
                    page.closeEditor(part, true);
                }
            }

        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Error while opening patients perpective", e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}
