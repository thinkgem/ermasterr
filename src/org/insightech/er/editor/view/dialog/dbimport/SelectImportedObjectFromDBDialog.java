package org.insightech.er.editor.view.dialog.dbimport;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbimport.DBObjectSet;

public class SelectImportedObjectFromDBDialog extends AbstractSelectImportedObjectDialog {

    private Button clearDiagramButton;

    private final ERDiagramEditor editor;

    private final ERDiagram diagram;

    public SelectImportedObjectFromDBDialog(final Shell parentShell, final ERDiagram diagram, final DBObjectSet allObjectSet, final ERDiagramEditor editor) {
        super(parentShell, diagram, allObjectSet);

        this.editor = editor;
        this.diagram = diagram;
    }

    @Override
    protected void initializeOptionGroup(final Group group) {
        clearDiagramButton = CompositeFactory.createCheckbox(this, group, "label.clear.diagram", false);
        clearDiagramButton.setSelection(true);

        useCommentAsLogicalNameButton = CompositeFactory.createCheckbox(this, group, "label.use.comment.as.logical.name", false);
        useCommentAsLogicalNameButton.setSelection(true);
        super.initializeOptionGroup(group);
    }

    @Override
    protected void perfomeOK() throws InputException {
        super.perfomeOK();

        resultUseCommentAsLogicalName = useCommentAsLogicalNameButton.getSelection();

        if (clearDiagramButton.getSelection()) {
            if (!ERDiagramActivator.showConfirmDialog("label.clear.diagram.confirm")) {
                throw new InputException();
            } else {
                diagram.clear();
                editor.resetCommandStack();
            }
        }
    }
}
