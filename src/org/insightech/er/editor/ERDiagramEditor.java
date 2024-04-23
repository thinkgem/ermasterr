package org.insightech.er.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.controller.editpart.element.PagableFreeformRootEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.ERDiagramPopupMenuManager;
import org.insightech.er.editor.view.action.category.CategoryManageAction;
import org.insightech.er.editor.view.action.category.ChangeFreeLayoutAction;
import org.insightech.er.editor.view.action.category.ChangeShowReferredTablesAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDDLAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDictionaryAction;
import org.insightech.er.editor.view.action.dbexport.ExportToExcelAction;
import org.insightech.er.editor.view.action.dbexport.ExportToHtmlAction;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.editor.view.action.dbexport.ExportToJavaAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTestDataAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTranslationDictionaryAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromDBAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromFileAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction;
import org.insightech.er.editor.view.action.edit.CopyAction;
import org.insightech.er.editor.view.action.edit.DeleteWithoutUpdateAction;
import org.insightech.er.editor.view.action.edit.EditAllAttributesAction;
import org.insightech.er.editor.view.action.edit.PasteAction;
import org.insightech.er.editor.view.action.edit.SelectAllContentsAction;
import org.insightech.er.editor.view.action.group.GroupManageAction;
import org.insightech.er.editor.view.action.line.AutoResizeModelAction;
import org.insightech.er.editor.view.action.line.DefaultLineAction;
import org.insightech.er.editor.view.action.line.ERDiagramAlignmentAction;
import org.insightech.er.editor.view.action.line.ERDiagramMatchHeightAction;
import org.insightech.er.editor.view.action.line.ERDiagramMatchWidthAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction;
import org.insightech.er.editor.view.action.line.RightAngleLineAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction;
import org.insightech.er.editor.view.action.option.OptionSettingAction;
import org.insightech.er.editor.view.action.option.notation.ChangeCapitalAction;
import org.insightech.er.editor.view.action.option.notation.ChangeNotationExpandGroupAction;
import org.insightech.er.editor.view.action.option.notation.ChangeStampAction;
import org.insightech.er.editor.view.action.option.notation.LockEditAction;
import org.insightech.er.editor.view.action.option.notation.TooltipAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFunnyAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToColumnAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToDetailAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToExcludeTypeAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToNameAndKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyTitleAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIDEF1XNotationAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIENotationAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToBothAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToLogicalAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToPhysicalAction;
import org.insightech.er.editor.view.action.printer.PageSettingAction;
import org.insightech.er.editor.view.action.printer.PrintImageAction;
import org.insightech.er.editor.view.action.search.SearchAction;
import org.insightech.er.editor.view.action.testdata.TestDataCreateAction;
import org.insightech.er.editor.view.action.tracking.ChangeTrackingAction;
import org.insightech.er.editor.view.action.translation.TranslationManageAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustAction;
import org.insightech.er.editor.view.contributor.ERDiagramActionBarContributor;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDropTargetListener;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePage;
import org.insightech.er.editor.view.outline.ERDiagramOutlinePopupMenuManager;
import org.insightech.er.editor.view.tool.ERDiagramPaletteRoot;
import org.insightech.er.extention.ExtensionLoader;

/**
 * TODO ON UPDATE、ON DELETE のプルダウンを設定できるものだけに制限する<br>
 * TODO デフォルト値に型の制限を適用する<br>
 */
public class ERDiagramEditor extends GraphicalEditorWithPalette {

    private final ERDiagram diagram;

    private final ERDiagramEditPartFactory editPartFactory;

    private final ERDiagramOutlinePage outlinePage;

    private MenuManager outlineMenuMgr;

    private ERDiagramActionBarContributor actionBarContributor;

    private final ERDiagramPaletteRoot palette;

    private ExtensionLoader extensionLoader;

    private boolean isDirty;

    /**
     * コンストラクタ.
     * 
     * @param diagram
     *            ERDiagram
     * @param editPartFactory
     *            ERDiagramEditPartFactory
     * @param outlinePage
     *            ERDiagramOutlinePage
     * @param editDomain
     *            DefaultEditDomain
     */
    public ERDiagramEditor(final ERDiagram diagram, final ERDiagramEditPartFactory editPartFactory, final ERDiagramOutlinePage outlinePage, final DefaultEditDomain editDomain, final ERDiagramPaletteRoot palette) {
        this.diagram = diagram;
        this.editPartFactory = editPartFactory;
        this.outlinePage = outlinePage;
        this.palette = palette;

        setEditDomain(editDomain);

        try {
            extensionLoader = new ExtensionLoader(this);
        } catch (final CoreException e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        getSelectionSynchronizer().removeViewer(outlinePage.getViewer());
        super.dispose();
    }

    /**
     * <pre>
     * 保存時の処理
     * ファイルの保存自体は、{@link ERDiagramMultiPageEditor} で行うため
     * 各ページの {@link ERDiagramEditor} では、コマンドスタックのクリアのみを行う
     * </pre>
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        getCommandStack().markSaveLocation();
        isDirty = false;
    }

    public void resetCommandStack() {
        getCommandStack().flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commandStackChanged(final EventObject eventObject) {
        firePropertyChange(IEditorPart.PROP_DIRTY);
        super.commandStackChanged(eventObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeGraphicalViewer() {
        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setEditPartFactory(editPartFactory);

        initViewerAction(viewer);
        initDragAndDrop(viewer);

        viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, false);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, false);
        viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, true);

        final MenuManager menuMgr = new ERDiagramPopupMenuManager(getActionRegistry(), diagram);

        extensionLoader.addERDiagramPopupMenu(menuMgr, getActionRegistry());

        viewer.setContextMenu(menuMgr);

        viewer.setContents(diagram);

        outlineMenuMgr = new ERDiagramOutlinePopupMenuManager(diagram, getActionRegistry(), outlinePage.getOutlineActionRegistory(), outlinePage.getViewer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PaletteRoot getPaletteRoot() {
        return palette;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Class type) {
        if (type == ZoomManager.class) {
            return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();

        } else if (type == IContentOutlinePage.class) {
            return outlinePage;
        }

        return super.getAdapter(type);
    }

    /**
     * <pre>
     * このページが選択された際の処理
     * </pre>
     */
    public void changeCategory() {
        outlinePage.setCategory(getEditDomain(), getGraphicalViewer(), outlineMenuMgr, getActionRegistry());

        getSelectionSynchronizer().addViewer(outlinePage.getViewer());

        getEditDomain().setPaletteViewer(getPaletteViewer());

        getActionRegistry().getAction(TooltipAction.ID).setChecked(diagram.isTooltip());
        getActionRegistry().getAction(LockEditAction.ID).setChecked(diagram.isDisableSelectColumn());

        ((ChangeBackgroundColorAction) getActionRegistry().getAction(ChangeBackgroundColorAction.ID)).setRGB();

    }

    public void removeSelection() {
        getGraphicalViewer().deselectAll();
        getSelectionSynchronizer().removeViewer(outlinePage.getViewer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void createActions() {
        super.createActions();

        final ActionRegistry registry = getActionRegistry();
        final List<String> selectionActionList = getSelectionActions();

        final List<IAction> actionList = new ArrayList<IAction>(Arrays.asList(new IAction[] {new ChangeViewToLogicalAction(this), new ChangeViewToPhysicalAction(this), new ChangeViewToBothAction(this), new ChangeToIENotationAction(this), new ChangeToIDEF1XNotationAction(this), new ChangeNotationLevelToColumnAction(this), new ChangeNotationLevelToExcludeTypeAction(this), new ChangeNotationLevelToDetailAction(this), new ChangeNotationLevelToOnlyTitleAction(this), new ChangeNotationLevelToOnlyKeyAction(this), new ChangeNotationLevelToNameAndKeyAction(this), new ChangeNotationExpandGroupAction(this), new ChangeDesignToFunnyAction(this), new ChangeDesignToFrameAction(this), new ChangeDesignToSimpleAction(this), new ChangeCapitalAction(this), new ChangeStampAction(this), new GroupManageAction(this), new ChangeTrackingAction(this), new OptionSettingAction(this), new CategoryManageAction(this), new ChangeFreeLayoutAction(this), new ChangeShowReferredTablesAction(this), new TranslationManageAction(this), new TestDataCreateAction(this), new ImportFromDBAction(this), new ImportFromFileAction(this), new ExportToImageAction(this), new ExportToExcelAction(this), new ExportToHtmlAction(this), new ExportToJavaAction(this), new ExportToDDLAction(this), new ExportToDictionaryAction(this), new ExportToTranslationDictionaryAction(this), new ExportToTestDataAction(this), new PageSettingAction(this), new EditAllAttributesAction(this), new DirectEditAction((IWorkbenchPart) this), new ERDiagramAlignmentAction(this, PositionConstants.LEFT), new ERDiagramAlignmentAction(this, PositionConstants.CENTER), new ERDiagramAlignmentAction(this, PositionConstants.RIGHT), new ERDiagramAlignmentAction(this, PositionConstants.TOP), new ERDiagramAlignmentAction(this, PositionConstants.MIDDLE), new ERDiagramAlignmentAction(this, PositionConstants.BOTTOM), new ERDiagramMatchWidthAction(this), new ERDiagramMatchHeightAction(this), new HorizontalLineAction(this), new VerticalLineAction(this), new RightAngleLineAction(this), new DefaultLineAction(this), new CopyAction(this), new PasteAction(this), new SearchAction(this), new AutoResizeModelAction(this), new PrintImageAction(this), new DeleteWithoutUpdateAction(this), new SelectAllContentsAction(this)}));

        actionList.addAll(extensionLoader.createExtendedActions());

        for (final IAction action : actionList) {
            if (action instanceof SelectionAction) {
                final IAction originalAction = registry.getAction(action.getId());

                if (originalAction != null) {
                    selectionActionList.remove(originalAction);
                }
                selectionActionList.add(action.getId());
            }

            registry.registerAction(action);
        }

        final IAction action = registry.getAction(SearchAction.ID);
        addKeyHandler(action);
    }

    @SuppressWarnings("unchecked")
    private void initViewerAction(final GraphicalViewer viewer) {
        final ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(diagram);
        viewer.setRootEditPart(rootEditPart);

        final ZoomManager manager = rootEditPart.getZoomManager();

        final double[] zoomLevels = new double[] {0.1, 0.25, 0.5, 0.75, 0.8, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0};
        manager.setZoomLevels(zoomLevels);

        final List<String> zoomContributions = new ArrayList<String>();
        zoomContributions.add(ZoomManager.FIT_ALL);
        zoomContributions.add(ZoomManager.FIT_HEIGHT);
        zoomContributions.add(ZoomManager.FIT_WIDTH);
        manager.setZoomLevelContributions(zoomContributions);

        final ZoomInAction zoomInAction = new ZoomInAction(manager);
        final ZoomOutAction zoomOutAction = new ZoomOutAction(manager);
        final ZoomAdjustAction zoomAdjustAction = new ZoomAdjustAction(manager);

        getActionRegistry().registerAction(zoomInAction);
        getActionRegistry().registerAction(zoomOutAction);
        getActionRegistry().registerAction(zoomAdjustAction);

        addKeyHandler(zoomInAction);
        addKeyHandler(zoomOutAction);

//        final IFigure gridLayer = rootEditPart.getLayer(LayerConstants.GRID_LAYER);
//        gridLayer.setForegroundColor(Resources.GRID_COLOR);

        IAction action = new ToggleGridAction(viewer);
        getActionRegistry().registerAction(action);

        action = new ToggleSnapToGeometryAction(viewer);
        getActionRegistry().registerAction(action);

        action = new ChangeBackgroundColorAction(this, diagram);
        getActionRegistry().registerAction(action);
        getSelectionActions().add(action.getId());

        action = new TooltipAction(this);
        getActionRegistry().registerAction(action);

        action = new LockEditAction(this);
        getActionRegistry().registerAction(action);

        action = new ExportToDBAction(this);
        getActionRegistry().registerAction(action);

        actionBarContributor = new ERDiagramActionBarContributor();
        actionBarContributor.init(getEditorSite().getActionBars(), getSite().getPage());
        // action = new ToggleRulerVisibilityAction(viewer);
        // this.getActionRegistry().registerAction(action);
    }

    private void initDragAndDrop(final GraphicalViewer viewer) {
        final AbstractTransferDragSourceListener dragSourceListener = new ERDiagramTransferDragSourceListener(viewer, TemplateTransfer.getInstance());
        viewer.addDragSourceListener(dragSourceListener);

        final AbstractTransferDropTargetListener dropTargetListener = new ERDiagramTransferDropTargetListener(viewer, TemplateTransfer.getInstance());

        viewer.addDropTargetListener(dropTargetListener);
    }

    private void addKeyHandler(final IAction action) {
        final IHandlerService service = getSite().getService(IHandlerService.class);
        service.activateHandler(action.getActionDefinitionId(), new ActionHandler(action));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphicalViewer getGraphicalViewer() {
        return super.getGraphicalViewer();
    }

    public void resetEditDomain() {
        getEditDomain().setPaletteViewer(getPaletteViewer());
    }

    public ERDiagramActionBarContributor getActionBarContributor() {
        return actionBarContributor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        final IEditorPart editorPart = getSite().getPage().getActiveEditor();

        if (editorPart instanceof ERDiagramMultiPageEditor) {
            final ERDiagramMultiPageEditor multiPageEditorPart = (ERDiagramMultiPageEditor) editorPart;

            if (equals(multiPageEditorPart.getActiveEditor())) {
                updateActions(getSelectionActions());
            }

        } else {
            super.selectionChanged(part, selection);
        }
    }

    public Point getLocation() {
        final FigureCanvas canvas = (FigureCanvas) getGraphicalViewer().getControl();
        return canvas.getViewport().getViewLocation();
    }

    public void setLocation(final int x, final int y) {
        final FigureCanvas canvas = (FigureCanvas) getGraphicalViewer().getControl();
        canvas.scrollTo(x, y);
    }

    public void setDirty(final boolean isDirty) {
        this.isDirty = isDirty;
    }

    @Override
    public boolean isDirty() {
        if (isDirty) {
            return true;
        }

        return super.isDirty();
    }

    public String getProjectFilePath(final String extention) {
        final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
        String filePath = file.getLocation().toOSString();
        filePath = filePath.substring(0, filePath.lastIndexOf(".")) + extention;

        return filePath;
    }

}
