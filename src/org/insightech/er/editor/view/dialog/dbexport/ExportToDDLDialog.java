package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.dbexport.ExportWithProgressManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.dbexport.ddl.ExportToDDLManager;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToDDLDialog extends AbstractExportDialog {

    private Combo environmentCombo;

    private FileText outputFileText;

    private Combo fileEncodingCombo;

    private Combo lineFeedCombo;

    private Combo categoryCombo;
//    private Label categoryLabel;

    private Button inlineTableComment;

    private Button inlineColumnComment;

    private Button dropTablespace;

    private Button dropSequence;

    private Button dropTrigger;

    private Button dropView;

    private Button dropIndex;

    private Button dropTable;

    private Button createTablespace;

    private Button createSequence;

    private Button createTrigger;

    private Button createView;

    private Button createIndex;

    private Button createTable;

    private Button createForeignKey;

    private Button createComment;

    private Button commentValueDescription;

    private Button commentValueLogicalName;

    private Button commentValueLogicalNameDescription;

    private Button commentReplaceLineFeed;

    private Text commentReplaceString;
    
    private List<Button> outputDababaseList = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize(final Composite parent) {
        environmentCombo = CompositeFactory.createReadOnlyCombo(this, parent, "label.tablespace.environment", 2, -1);
        for (final Environment environment : settings.getEnvironmentSetting().getEnvironments()) {
            environmentCombo.add(environment.getName());
        }

        outputFileText = CompositeFactory.createFileText(true, this, parent, "label.output.file", getBaseDir(), getDefaultOutputFileName(".sql"), "*.sql");

        fileEncodingCombo = CompositeFactory.createFileEncodingCombo(diagram.getEditor().getDefaultCharset(), this, parent, "label.output.file.encoding", 2);

        lineFeedCombo = CompositeFactory.createReadOnlyCombo(this, parent, "label.line.feed.code", 2);
        lineFeedCombo.add(ExportDDLSetting.CRLF);
        lineFeedCombo.add(ExportDDLSetting.LF);

//        CompositeFactory.createLabel(parent, "label.category");
//        categoryLabel = CompositeFactory.createLabelAsValue(parent, "", 2);
        categoryCombo = CompositeFactory.createReadOnlyCombo(this, parent, "label.category", 2, -1);
        initCategoryCombo(this.categoryCombo);

        createCheckboxComposite(parent);

        createCommentComposite(parent);

        createOutputDababaseButton(parent); // ThinkGem
        
        final Composite checkboxArea = this.createCheckboxArea(parent, false);
        
        createOpenAfterSavedButton(checkboxArea, false, 3);
    }

    private void createCheckboxComposite(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);

        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.numColumns = 2;
        composite.setLayout(layout);

        createDropCheckboxGroup(composite);
        createCreateCheckboxGroup(composite);
    }

    private void createDropCheckboxGroup(final Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        group.setLayoutData(gridData);

        group.setText("DROP");

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        dropTablespace = CompositeFactory.createCheckbox(this, group, "label.tablespace", false);
        dropSequence = CompositeFactory.createCheckbox(this, group, "label.sequence", false);
        dropTrigger = CompositeFactory.createCheckbox(this, group, "label.trigger", false);
        dropView = CompositeFactory.createCheckbox(this, group, "label.view", false);
        dropIndex = CompositeFactory.createCheckbox(this, group, "label.index", false);
        dropTable = CompositeFactory.createCheckbox(this, group, "label.table", false);
    }

    private void createCreateCheckboxGroup(final Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        group.setLayoutData(gridData);

        group.setText("CREATE");

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);

        createTablespace = CompositeFactory.createCheckbox(this, group, "label.tablespace", false);
        createSequence = CompositeFactory.createCheckbox(this, group, "label.sequence", false);
        createTrigger = CompositeFactory.createCheckbox(this, group, "label.trigger", false);
        createView = CompositeFactory.createCheckbox(this, group, "label.view", false);
        createIndex = CompositeFactory.createCheckbox(this, group, "label.index", false);
        createTable = CompositeFactory.createCheckbox(this, group, "label.table", false);
        createForeignKey = CompositeFactory.createCheckbox(this, group, "label.foreign.key", false);
        createComment = CompositeFactory.createCheckbox(this, group, "label.comment", false);
    }

    private void createCommentComposite(final Composite parent) {
        final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);

        final GridLayout compositeLayout = new GridLayout();
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setText(ResourceString.getResourceString("label.comment"));

        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        group.setLayout(layout);

        final GridData commentValueGridData = new GridData();
        commentValueGridData.horizontalSpan = 3;
        commentValueGridData.horizontalAlignment = GridData.FILL;
        commentValueGridData.grabExcessHorizontalSpace = true;

        final Group commentValueGroup = new Group(group, SWT.NONE);
        commentValueGroup.setLayoutData(commentValueGridData);
        commentValueGroup.setText(ResourceString.getResourceString("label.comment.value"));

        final GridLayout commentValueLayout = new GridLayout();
        commentValueLayout.numColumns = 1;
        commentValueGroup.setLayout(commentValueLayout);

        commentValueDescription = CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.description");
        commentValueLogicalName = CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.logical.name");
        commentValueLogicalNameDescription = CompositeFactory.createRadio(this, commentValueGroup, "label.comment.value.logical.name.description");

        commentReplaceLineFeed = CompositeFactory.createCheckbox(this, group, "label.comment.replace.line.feed", false);
        commentReplaceString = CompositeFactory.createText(this, group, "label.comment.replace.string", 1, 20, false, false);

        inlineTableComment = CompositeFactory.createCheckbox(this, group, "label.comment.inline.table", false, 4);
        inlineColumnComment = CompositeFactory.createCheckbox(this, group, "label.comment.inline.column", false, 4);
    }
    
    // ThinkGem 输出其它数据库类型 按钮创建
    private void createOutputDababaseButton(final Composite parent){
    	final GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(gridData);

        final GridLayout compositeLayout = new GridLayout();
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);

        final Group group = new Group(composite, SWT.NONE);
        group.setLayoutData(gridData);
        group.setText("Batch output database types");

        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        group.setLayout(layout);
        
        List<String> databaseList = DBManagerFactory.getAllDBList();
        for (String database : databaseList) {
        	outputDababaseList.add(CompositeFactory.createCheckbox(this, group, database, false));
		}
    }

    @Override
    protected String getErrorMessage() {
        if (isBlank(environmentCombo)) {
            return "error.tablespace.environment.empty";
        }

        if (outputFileText.isBlank()) {
            return "error.output.file.is.empty";
        }

        if (!Charset.isSupported(fileEncodingCombo.getText())) {
            return "error.file.encoding.is.not.supported";
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setData() {
        final ExportDDLSetting exportDDLSetting = settings.getExportSetting().getExportDDLSetting();

        String outputFile = Format.null2blank(exportDDLSetting.getDdlOutput());

        if (Check.isEmpty(outputFile)) {
            outputFile = getDefaultOutputFilePath(".sql");
        }

        outputFileText.setText(FileUtils.getRelativeFilePath(getBaseDir(), outputFile));

        this.setCategoryComboData(this.categoryCombo, exportDDLSetting.getCategory());
//        setCategoryData(categoryLabel);

        final DDLTarget ddlTarget = exportDDLSetting.getDdlTarget();

        dropIndex.setSelection(ddlTarget.dropIndex);
        dropSequence.setSelection(ddlTarget.dropSequence);
        dropTable.setSelection(ddlTarget.dropTable);
        dropTablespace.setSelection(ddlTarget.dropTablespace);
        dropTrigger.setSelection(ddlTarget.dropTrigger);
        dropView.setSelection(ddlTarget.dropView);
        createComment.setSelection(ddlTarget.createComment);
        createForeignKey.setSelection(ddlTarget.createForeignKey);
        createIndex.setSelection(ddlTarget.createIndex);
        createSequence.setSelection(ddlTarget.createSequence);
        createTable.setSelection(ddlTarget.createTable);
        createTablespace.setSelection(ddlTarget.createTablespace);
        createTrigger.setSelection(ddlTarget.createTrigger);
        createView.setSelection(ddlTarget.createView);
        inlineColumnComment.setSelection(ddlTarget.inlineColumnComment);
        inlineTableComment.setSelection(ddlTarget.inlineTableComment);
        commentReplaceLineFeed.setSelection(ddlTarget.commentReplaceLineFeed);
        commentReplaceString.setText(Format.null2blank(ddlTarget.commentReplaceString));
        commentValueDescription.setSelection(ddlTarget.commentValueDescription);
        commentValueLogicalName.setSelection(ddlTarget.commentValueLogicalName);
        commentValueLogicalNameDescription.setSelection(ddlTarget.commentValueLogicalNameDescription);

        if (!ddlTarget.commentValueDescription && !ddlTarget.commentValueLogicalName && !ddlTarget.commentValueLogicalNameDescription) {
            commentValueDescription.setSelection(true);
        }

        environmentCombo.select(0);

        if (exportDDLSetting.getEnvironment() != null) {
            final int index = settings.getEnvironmentSetting().getEnvironments().indexOf(exportDDLSetting.getEnvironment());

            if (index != -1) {
                environmentCombo.select(index);
            }
        }

        if (!Check.isEmpty(exportDDLSetting.getSrcFileEncoding())) {
            fileEncodingCombo.setText(exportDDLSetting.getSrcFileEncoding());
        }

        if (!Check.isEmpty(exportDDLSetting.getLineFeed())) {
            lineFeedCombo.setText(exportDDLSetting.getLineFeed());

        } else {
            if ("\n".equals(System.getProperty("line.separator"))) {
                lineFeedCombo.setText(ExportDDLSetting.LF);
            } else {
                lineFeedCombo.setText(ExportDDLSetting.CRLF);
            }
        }

        openAfterSavedButton.setSelection(exportDDLSetting.isOpenAfterSaved());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.export.ddl";
    }

    @Override
    protected ExportWithProgressManager getExportWithProgressManager(final ExportSetting exportSetting) throws InputException {
        final ExportDDLSetting exportDDLSetting = exportSetting.getExportDDLSetting();

        final String saveFilePath = outputFileText.getFilePath();

        // File outputFile = FileUtils.getFile(this.getProjectDir(),
        // saveFilePath);
        // File outputDir = outputFile.getParentFile();
        //
        // if (!outputDir.exists()) {
        // if (!Activator.showConfirmDialog(ResourceString.getResourceString(
        // "dialog.message.create.parent.dir",
        // new String[] { outputDir.getAbsolutePath() }))) {
        // throw new InputException();
        //
        // } else {
        // outputDir.mkdirs();
        // }
        // }

        exportDDLSetting.setDdlOutput(saveFilePath);
        exportDDLSetting.setOpenAfterSaved(openAfterSavedButton.getSelection());

        exportDDLSetting.setCategory(this.getSelectedCategory(this.categoryCombo));
        //exportDDLSetting.setCategory(diagram.getCurrentCategory());
        
        List<String> outputDababaseIdList = new ArrayList<>();
        for (Button button : outputDababaseList) {
        	if (button.getSelection()){
        		outputDababaseIdList.add(button.getText());
        	}
		}
        exportDDLSetting.setOutputDababaseIdList(outputDababaseIdList);

        final int index = environmentCombo.getSelectionIndex();
        final Environment environment = settings.getEnvironmentSetting().getEnvironments().get(index);
        exportDDLSetting.setEnvironment(environment);

        exportDDLSetting.setSrcFileEncoding(fileEncodingCombo.getText());
        exportDDLSetting.setLineFeed(lineFeedCombo.getText());

        exportDDLSetting.setDdlTarget(createDDLTarget());

        return new ExportToDDLManager(exportDDLSetting);
    }

    private DDLTarget createDDLTarget() {
        final DDLTarget ddlTarget = new DDLTarget();

        ddlTarget.dropTablespace = dropTablespace.getSelection();
        ddlTarget.dropSequence = dropSequence.getSelection();
        ddlTarget.dropTrigger = dropTrigger.getSelection();
        ddlTarget.dropView = dropView.getSelection();
        ddlTarget.dropIndex = dropIndex.getSelection();
        ddlTarget.dropTable = dropTable.getSelection();
        ddlTarget.createTablespace = createTablespace.getSelection();
        ddlTarget.createSequence = createSequence.getSelection();
        ddlTarget.createTrigger = createTrigger.getSelection();
        ddlTarget.createView = createView.getSelection();
        ddlTarget.createIndex = createIndex.getSelection();
        ddlTarget.createTable = createTable.getSelection();
        ddlTarget.createForeignKey = createForeignKey.getSelection();
        ddlTarget.createComment = createComment.getSelection();
        ddlTarget.inlineTableComment = inlineTableComment.getSelection();
        ddlTarget.inlineColumnComment = inlineColumnComment.getSelection();
        ddlTarget.commentReplaceLineFeed = commentReplaceLineFeed.getSelection();
        ddlTarget.commentReplaceString = commentReplaceString.getText();
        ddlTarget.commentValueDescription = commentValueDescription.getSelection();
        ddlTarget.commentValueLogicalName = commentValueLogicalName.getSelection();
        ddlTarget.commentValueLogicalNameDescription = commentValueLogicalNameDescription.getSelection();

        return ddlTarget;
    }

    @Override
    protected void perfomeOK() throws Exception {
        final Validator validator = new Validator();

        final List<ValidateResult> errorList = validator.validate(diagram);

        if (!errorList.isEmpty()) {
            final ExportWarningDialog dialog = new ExportWarningDialog(getShell(), errorList);

            if (dialog.open() != IDialogConstants.OK_ID) {
                throw new InputException();
            }
        }

        super.perfomeOK();
    }

    @Override
    protected File openAfterSaved() {
        final File file = FileUtils.getFile(getBaseDir(), settings.getExportSetting().getExportDDLSetting().getDdlOutput());

        return file;
    }

}
