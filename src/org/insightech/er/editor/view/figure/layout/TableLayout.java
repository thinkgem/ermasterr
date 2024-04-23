package org.insightech.er.editor.view.figure.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class TableLayout extends AbstractHintLayout {

    private int colnum;

    private final int separatorWidth;

    private final List<IFigure> separators;

    public TableLayout(final int colnum) {
        super();

        this.colnum = colnum;
        if (this.colnum <= 0) {
            this.colnum = 1;
        }

        separators = new ArrayList<IFigure>();
        separatorWidth = 1;
    }

    public void setSeparator() {

    }

    @Override
    public void layout(final IFigure parent) {

        final List children = clearSeparator(parent);

        final List<List<IFigure>> table = getTable(children);
        final int[] columnWidth = getColumnWidth(table);
        final int[] rowHeight = getRowHeight(table);

        final Rectangle rect = parent.getBounds();

        int x = rect.x + 1;
        int y = rect.y + 1;

        for (int i = 0; i < table.size(); i++) {
            final List<IFigure> tableRow = table.get(i);

            for (int j = 0; j < tableRow.size(); j++) {
                final Rectangle childRect = new Rectangle(x, y, columnWidth[j], rowHeight[i]);

                final IFigure figure = tableRow.get(j);
                figure.setBounds(childRect);

                x += columnWidth[j];

                if (j != tableRow.size() - 1) {
                    final Rectangle separetorRect = new Rectangle(x, y, separatorWidth, rowHeight[i]);
                    addVerticalSeparator(parent, separetorRect);

                    x += separatorWidth;
                }

            }

            x = rect.x + 1;
            y += rowHeight[i];

            if (i != table.size() - 1) {
                final Rectangle separetorRect = new Rectangle(x, y, rect.width, separatorWidth);

                addHorizontalSeparator(parent, separetorRect);

                y += separatorWidth;
            }
        }
    }

    private List<List<IFigure>> getTable(final List children) {
        final int numChildren = children.size();

        final List<List<IFigure>> table = new ArrayList<List<IFigure>>();

        List<IFigure> row = null;

        for (int i = 0; i < numChildren; i++) {
            if (i % colnum == 0) {
                row = new ArrayList<IFigure>();
                table.add(row);
            }

            row.add((IFigure) children.get(i));
        }

        return table;
    }

    private int[] getColumnWidth(final List<List<IFigure>> table) {
        final int[] columnWidth = new int[colnum];

        for (int i = 0; i < colnum; i++) {
            for (final List<IFigure> tableRow : table) {
                if (tableRow.size() > i) {
                    final IFigure figure = tableRow.get(i);

                    final int width = figure.getPreferredSize().width;

                    if (width > columnWidth[i]) {
                        columnWidth[i] = (int) (width * 1.3);
                    }
                }
            }
        }

        return columnWidth;
    }

    private int[] getRowHeight(final List<List<IFigure>> table) {
        final int[] rowHeight = new int[table.size()];

        for (int i = 0; i < rowHeight.length; i++) {
            for (final IFigure cell : table.get(i)) {
                final int height = cell.getPreferredSize().height;

                if (height > rowHeight[i]) {
                    rowHeight[i] = height;
                }
            }
        }

        return rowHeight;
    }

    private List<IFigure> getChildren(final IFigure parent) {
        final List<IFigure> children = new ArrayList<IFigure>();

        for (final Iterator iter = parent.getChildren().iterator(); iter.hasNext();) {
            final IFigure child = (IFigure) iter.next();

            if (!separators.contains(child)) {
                children.add(child);
            }
        }

        return children;
    }

    private List clearSeparator(final IFigure parent) {
        for (final Iterator iter = parent.getChildren().iterator(); iter.hasNext();) {
            final IFigure child = (IFigure) iter.next();

            if (separators.contains(child)) {
                iter.remove();
            }
        }

        separators.clear();

        return parent.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Dimension calculatePreferredSize(final IFigure container, final int wHint, final int hHint) {
        final List children = getChildren(container);

        final List<List<IFigure>> table = getTable(children);
        final int[] columnWidth = getColumnWidth(table);
        final int[] rowHeight = getRowHeight(table);

        int width = 0;
        for (int i = 0; i < columnWidth.length; i++) {
            width += columnWidth[i];
            if (i != columnWidth.length - 1) {
                width += separatorWidth;
            }
        }
        width++;
        width++;

        int height = 0;
        for (int i = 0; i < rowHeight.length; i++) {
            height += rowHeight[i];
            if (i != rowHeight.length - 1) {
                height += separatorWidth;
            }
        }
        height++;
        height++;

        return new Dimension(width, height);
    }

    @SuppressWarnings("unchecked")
    private void addVerticalSeparator(final IFigure figure, final Rectangle rect) {
        final Polyline separator = new Polyline();
        separator.setLineWidth(separatorWidth);
        separator.addPoint(new Point(rect.x, rect.y));
        separator.addPoint(new Point(rect.x, rect.y + rect.height));
        
        ((List)figure.getChildren()).add(separator);
        separator.setParent(figure);

        separators.add(separator);
    }

    @SuppressWarnings("unchecked")
    private void addHorizontalSeparator(final IFigure figure, final Rectangle rect) {
        final Polyline separator = new Polyline();
        separator.setLineWidth(separatorWidth);
        separator.addPoint(new Point(rect.x, rect.y));
        separator.addPoint(new Point(rect.x + rect.width, rect.y));

        ((List)figure.getChildren()).add(separator);
        separator.setParent(figure);

        separators.add(separator);
    }

}
