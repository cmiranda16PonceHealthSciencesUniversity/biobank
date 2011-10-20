package edu.ualberta.med.biobank.mvp.view;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class SiteEntryView implements SiteEntryPresenter.View {
    private SiteEntryForm widget;

    private final ButtonItem save = new ButtonItem();
    private final ButtonItem reload = new ButtonItem();
    private final ButtonItem close = new ButtonItem();
    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final TextItem comment = new TextItem();
    private final TableItem<List<StudyInfo>> studies = new TableItem<List<StudyInfo>>();

    private BaseView addressEntryView;
    private BaseView activityStatusComboView;

    @Override
    public HasButton getSave() {
        return save;
    }

    @Override
    public HasValue<String> getName() {
        return name;
    }

    @Override
    public HasValue<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasValue<String> getComment() {
        return comment;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        return null;
    }

    @Override
    public void close() {
        widget.getParent().dispose();
    }

    @Override
    public HasClickHandlers getClose() {
        return close;
    }

    @Override
    public HasClickHandlers getReload() {
        return reload;
    }

    @Override
    public void create(Composite parent) {
        widget = new SiteEntryForm(parent, SWT.NONE);

        name.setText(widget.name);
        save.setButton(widget.save);
        reload.setButton(widget.reload);

        // create the inner widgets
        addressEntryView.create(widget);
        activityStatusComboView.create(widget);
    }

    @Override
    public void setAddressEntryView(BaseView view) {
        this.addressEntryView = view;
    }

    @Override
    public void setActivityStatusComboView(BaseView view) {
        this.activityStatusComboView = view;
    }

    // TODO: move out
    public static class BaseForm extends Composite {
        private static final String PAGE_KEY = "page"; //$NON-NLS-1$
        protected final ManagedForm managedForm;
        protected final ScrolledForm form;
        protected final FormToolkit toolkit;
        protected final ScrolledPageBook book;
        protected final Composite page;

        public BaseForm(Composite parent, int style) {
            super(parent, style);

            managedForm = new ManagedForm(parent);
            toolkit = managedForm.getToolkit();
            form = managedForm.getForm();
            toolkit.decorateFormHeading(form.getForm());

            form.getBody().setLayout(new GridLayout());
            GridData gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.grabExcessVerticalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
            gd.verticalAlignment = SWT.FILL;
            form.getBody().setLayoutData(gd);

            book = toolkit.createPageBook(form.getBody(), SWT.V_SCROLL);
            book.setLayout(new GridLayout());
            book.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
                true));
            page = book.createPage(PAGE_KEY);
            book.showPage(PAGE_KEY);
            //
            // // start a new runnable so that database objects are populated in
            // a
            // // separate thread.
            // BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            // @Override
            // public void run() {
            // try {
            // form.setImage(getFormImage());
            // createFormContent();
            // form.reflow(true);
            // } catch (final RemoteConnectFailureException exp) {
            // BgcPlugin.openRemoteConnectErrorMessage(exp);
            // } catch (Exception e) {
            // BgcPlugin.openError(
            //                            "BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$
            // }
            // }
            // });
        }

    }

    public static class SiteEntryForm extends BaseForm {
        public final Text name;
        public final Button save;
        public final Button reload;

        public SiteEntryForm(Composite parent, int style) {
            super(parent, style);

            form.setText(Messages.StudyEntryForm_main_title);
            form.setMessage("asdfadsf", IMessageProvider.NONE);
            page.setLayout(new GridLayout(1, false));

            Composite client = toolkit.createComposite(page);
            GridLayout layout = new GridLayout(2, false);
            layout.horizontalSpacing = 10;
            client.setLayout(layout);
            client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            toolkit.paintBordersFor(client);

            name = new Text(client, SWT.NONE);
            save = new Button(client, SWT.NONE);
            save.setText("save");
            reload = new Button(client, SWT.NONE);
            reload.setText("reload");

            form.reflow(true);
        }
    }
}
