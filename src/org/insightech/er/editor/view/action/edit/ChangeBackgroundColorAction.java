package org.insightech.er.editor.view.action.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.display.ChangeBackgroundColorCommand;
import org.insightech.er.editor.controller.command.display.ChangeConnectionColorCommand;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ChangeBackgroundColorAction extends SelectionAction {

    public static final String ID = ChangeBackgroundColorAction.class.getName();

    private RGB rgb;

    private Image image;

    public ChangeBackgroundColorAction(final IWorkbenchPart part, final ERDiagram diagram) {
        super(part, IAction.AS_DROP_DOWN_MENU);

        setId(ID);

        setText(ResourceString.getResourceString("action.title.change.background.color"));
        setToolTipText(ResourceString.getResourceString("action.title.change.background.color"));

        final int[] defaultColor = diagram.getDefaultColor();

        rgb = new RGB(defaultColor[0], defaultColor[1], defaultColor[2]);
        setColorToImage();
    }

    private void setColorToImage() {
        final ImageData imageData = ERDiagramActivator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR).getImageData();
        final int blackPixel = imageData.palette.getPixel(new RGB(0, 0, 0));
        imageData.transparentPixel = imageData.palette.getPixel(new RGB(255, 255, 255));
        imageData.palette.colors[blackPixel] = rgb;

        if (this.image != null) {
            this.image.dispose();
        }
        
        image = new Image(Display.getCurrent(), imageData);

        final ImageDescriptor descriptor = ImageDescriptor.createFromImage(image);
        setImageDescriptor(descriptor);
    }

    private void setRGB(final RGB rgb) {
        this.rgb = rgb;

        final EditPart editPart = ((ERDiagramEditor) getWorkbenchPart()).getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();
        diagram.setDefaultColor(this.rgb.red, this.rgb.green, this.rgb.blue);

        setColorToImage();
    }

    public void setRGB() {
        final EditPart editPart = ((ERDiagramEditor) getWorkbenchPart()).getGraphicalViewer().getContents();
        final ERDiagram diagram = (ERDiagram) editPart.getModel();

        rgb = diagram.getDefaultColorAsGRB();

        setColorToImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runWithEvent(final Event event) {
        final Command command = createCommand(getSelectedObjects(), rgb);
        getCommandStack().execute(command);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List getSelectedObjects() {
        final List objects = new ArrayList(super.getSelectedObjects());
        for (final Iterator iter = objects.iterator(); iter.hasNext();) {
            if (iter.next() instanceof NormalColumnEditPart) {
                iter.remove();
            }
        }
        return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        final List objects = getSelectedObjects();

        if (objects.isEmpty()) {
            return false;
        }

        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return false;
        }

        return true;
    }

    private Command createCommand(final List objects, final RGB rgb) {
        if (objects.isEmpty()) {
            return null;
        }

        if (!(objects.get(0) instanceof GraphicalEditPart)) {
            return null;
        }

        final CompoundCommand command = new CompoundCommand();

        for (int i = 0; i < objects.size(); i++) {
            final GraphicalEditPart part = (GraphicalEditPart) objects.get(i);
            final Object modelObject = part.getModel();

            if (modelObject instanceof ViewableModel) {
                command.add(new ChangeBackgroundColorCommand((ViewableModel) modelObject, rgb.red, rgb.green, rgb.blue));

            } else if (modelObject instanceof ConnectionElement) {
                command.add(new ChangeConnectionColorCommand((ConnectionElement) modelObject, rgb.red, rgb.green, rgb.blue));

            }
        }

        return command;
    }

    public static class ChangeBackgroundColorRetargetAction extends LabelRetargetAction {
        public ChangeBackgroundColorRetargetAction() {
            super(ID, ResourceString.getResourceString("action.title.change.background.color"), IAction.AS_DROP_DOWN_MENU);

            setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR));
            setDisabledImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.CHANGE_BACKGROUND_COLOR_DISABLED));
            setToolTipText(ResourceString.getResourceString("action.title.change.background.color"));

            setMenuCreator(new IMenuCreator() {
                @Override
                public Menu getMenu(final Control parent) {
                    final Menu menu = new Menu(parent);

                    try {
                        final MenuItem item1 = new MenuItem(menu, SWT.NONE);
                        item1.setText(ResourceString.getResourceString("action.title.select.color"));
                        item1.setImage(ERDiagramActivator.getImage(ImageKey.PALETTE));

                        item1.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void widgetSelected(final SelectionEvent e) {
                                final ColorDialog colorDialog = new ColorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NULL);

                                colorDialog.setText(ResourceString.getResourceString("dialog.title.change.background.color"));

                                final ChangeBackgroundColorAction action = (ChangeBackgroundColorAction) getActionHandler();

                                final RGB rgb = colorDialog.open();

                                action.setRGB(rgb);
                                action.runWithEvent(null);
                            }
                        });
                    } catch (final Exception e) {
                        ERDiagramActivator.showExceptionDialog(e);
                    }
                    return menu;
                }

                @Override
                public Menu getMenu(final Menu parent) {
                    return null;
                }

                @Override
                public void dispose() {

                }
            });
        }
    }

    @Override
    public void dispose() {
        image.dispose();

        super.dispose();
    }
}
