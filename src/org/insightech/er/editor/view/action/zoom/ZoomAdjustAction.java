package org.insightech.er.editor.view.action.zoom;

import org.eclipse.draw2d.zoom.ZoomListener;
import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;

public class ZoomAdjustAction extends Action implements ZoomListener, Disposable {

    public static final String ID = ZoomAdjustAction.class.getName();

    protected ZoomManager zoomManager;

    public ZoomAdjustAction(final ZoomManager zoomManager) {
        super(ResourceString.getResourceString("action.title.zoom.adjust"), ERDiagramActivator.getImageDescriptor(ImageKey.ZOOM_ADJUST));
        this.zoomManager = zoomManager;
        zoomManager.addZoomListener(this);

        setToolTipText(ResourceString.getResourceString("action.title.zoom.adjust"));
        setId(ID);
    }

    @Override
    public void dispose() {
        zoomManager.removeZoomListener(this);
    }

    @Override
    public void run() {
        zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
    }

    @Override
    public void zoomChanged(final double zoom) {
        setEnabled(true);
    }

}
