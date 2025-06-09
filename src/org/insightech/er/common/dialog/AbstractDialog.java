package org.insightech.er.common.dialog;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.util.Check;

public abstract class AbstractDialog extends Dialog {

    private CLabel errorMessageText = null;

    private int numColumns;

    private boolean enabledOkButton = true;

    protected boolean initialized = false;

    protected AbstractDialog(final Shell parentShell) {
        super(parentShell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        getShell().setText(ResourceString.getResourceString(getTitle()));

        final Composite composite = (Composite) super.createDialogArea(parent);

        try {
            final GridLayout layout = new GridLayout();
            initLayout(layout);

            numColumns = layout.numColumns;

            composite.setLayout(layout);
            composite.setLayoutData(createLayoutData());

            createErrorComposite(composite);

            initialize(composite);

            initialized = true;

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }

        return composite;
    }

    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();
        setData();

        validate();
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Control control = super.createContents(parent);

        addListener();

        return control;
    }

    protected void initLayout(final GridLayout layout) {
        layout.numColumns = 2;
        layout.marginLeft = 20;
        layout.marginRight = 20;
        layout.marginBottom = 2;
    }

    protected int getNumColumns() {
        return numColumns;
    }

    protected int getErrorLine() {
        return 1;
    }

    protected Object createLayoutData() {
        return new GridData(GridData.FILL_BOTH);
    }

    protected void createErrorComposite(final Composite parent) {
        errorMessageText = new CLabel(parent, SWT.NONE);
        errorMessageText.setLeftMargin(0);
        errorMessageText.setText("");

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = 30 * getErrorLine();
        gridData.horizontalSpan = numColumns;

        errorMessageText.setLayoutData(gridData);
        errorMessageText.setForeground(ColorConstants.red);
    }

    protected Integer getIntegerValue(final Text text) {
        final String value = text.getText();
        if (Check.isEmpty(value)) {
            return null;
        }

        try {
            return Integer.valueOf(value.trim());

        } catch (final NumberFormatException e) {
            return null;
        }
    }

    final public boolean validate() {
        if (!initialized) {
            return true;
        }

        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(false);
        }

        final String errorMessage = getErrorMessage();

        if (errorMessage != null) {
            setMessage(ResourceString.getResourceString(errorMessage));
            return false;
        }

        if (okButton != null && enabledOkButton) {
            okButton.setEnabled(true);
        }

        setMessage(null);

        return true;
    }

    protected void setMessage(final String errorMessage) {
        if (errorMessageText != null) {
            if (errorMessage == null) {
                errorMessageText.setImage(null);
                errorMessageText.setText("");

            } else {
                // Image errorIcon =
                // JFaceResources.getImage("dialog_message_error_image");
                // this.errorMessageText.setImage(errorIcon);
                errorMessageText.setText("- " + errorMessage);

            }
        }
    }

    abstract protected void initialize(Composite composite);

    abstract protected void setData();

    protected void addListener() {}

    protected static boolean isBlank(final Text text) {
        if (text.getText().trim().length() == 0) {
            return true;
        }

        return false;
    }

    protected static boolean isBlank(final Combo combo) {
        if (combo.getText().trim().length() == 0) {
            return true;
        }

        return false;
    }

    protected void enabledButton(final boolean enabled) {
        enabledOkButton = enabled;

        final Button button1 = getButton(IDialogConstants.OK_ID);
        if (button1 != null) {
            button1.setEnabled(enabled);
        }

        final Button button2 = getButton(IDialogConstants.CANCEL_ID);
        if (button2 != null) {
            button2.setEnabled(enabled);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID || buttonId == IDialogConstants.CANCEL_ID || buttonId == IDialogConstants.BACK_ID) {
            setReturnCode(buttonId);
            close();

        } else if (buttonId == IDialogConstants.OK_ID) {
            try {
                if (!validate()) {
                    return;
                }

                perfomeOK();
                setReturnCode(buttonId);
                close();

            } catch (final InputException e) {
                if (e.getCause() != null) {
                    ERDiagramActivator.showErrorDialog(e.getCause().getMessage());

                } else if (e.getMessage() != null) {
                    setMessage(ResourceString.getResourceString(e.getMessage(), e.getArgs()));
                }
                return;

            } catch (final Exception e) {
                ERDiagramActivator.showExceptionDialog(e);
            }
        }

        super.buttonPressed(buttonId);
    }

    abstract protected String getErrorMessage();

    abstract protected void perfomeOK() throws Exception;

    abstract protected String getTitle();

    protected Button createCheckbox(final Composite composite, final String title) {
        return CompositeFactory.createCheckbox(this, composite, title, false, getNumColumns());
    }

}
