package org.insightech.er.db.impl.h2;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;

public class H2DDLCreator extends DDLCreator {

    public H2DDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        super(diagram, targetCategory, semicolon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCommentDDL(final ERTable table) {
        final List<String> ddlList = new ArrayList<String>();

        String tableComment = filterComment(table.getLogicalName(), table.getDescription(), false);
        tableComment = replaceLF(tableComment, LF());

        if (!Check.isEmpty(tableComment)) {
            final StringBuilder ddl = new StringBuilder();

            ddl.append("COMMENT ON TABLE ");
            ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
            ddl.append(" IS '");
            ddl.append(tableComment.replaceAll("'", "''"));
            ddl.append("'");
            if (semicolon) {
                ddl.append(";");
            }

            ddlList.add(ddl.toString());
        }

        for (final Column column : table.getColumns()) {
            if (column instanceof NormalColumn) {
                final NormalColumn normalColumn = (NormalColumn) column;

                String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);
                comment = replaceLF(comment, LF());

                if (!Check.isEmpty(comment)) {
                    final StringBuilder ddl = new StringBuilder();

                    ddl.append("COMMENT ON COLUMN ");
                    ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
                    ddl.append(".");
                    ddl.append(filterName(normalColumn.getPhysicalName()));
                    ddl.append(" IS '");
                    ddl.append(comment.replaceAll("'", "''"));
                    ddl.append("'");
                    if (semicolon) {
                        ddl.append(";");
                    }

                    ddlList.add(ddl.toString());
                }

            } else {
                final ColumnGroup columnGroup = (ColumnGroup) column;

                for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                    final String comment = filterComment(normalColumn.getLogicalName(), normalColumn.getDescription(), true);

                    if (!Check.isEmpty(comment)) {
                        final StringBuilder ddl = new StringBuilder();

                        ddl.append("COMMENT ON COLUMN ");
                        ddl.append(filterName(table.getNameWithSchema(getDiagram().getDatabase())));
                        ddl.append(".");
                        ddl.append(filterName(normalColumn.getPhysicalName()));
                        ddl.append(" IS '");
                        ddl.append(comment.replaceAll("'", "''"));
                        ddl.append("'");
                        if (semicolon) {
                            ddl.append(";");
                        }

                        ddlList.add(ddl.toString());
                    }
                }
            }
        }

        return ddlList;
    }

    @Override
    protected String getDDL(final Tablespace tablespace) {
        return null;
    }

    @Override
    public String getDDL(final Sequence sequence) {
        final StringBuilder ddl = new StringBuilder();

        final String description = sequence.getDescription();
        if (semicolon && !Check.isEmpty(description) && ddlTarget.inlineTableComment) {
            ddl.append("-- ");
            ddl.append(replaceLF(description, LF() + "-- "));
            ddl.append(LF());
        }

        ddl.append("CREATE ");
        ddl.append("SEQUENCE IF NOT EXISTS ");
        ddl.append(filterName(getNameWithSchema(sequence.getSchema(), sequence.getName())));
        if (sequence.getStart() != null) {
            ddl.append(" START WITH ");
            ddl.append(sequence.getStart());
        }
        if (sequence.getIncrement() != null) {
            ddl.append(" INCREMENT BY ");
            ddl.append(sequence.getIncrement());
        }
        if (sequence.getCache() != null) {
            ddl.append(" CACHE ");
            ddl.append(sequence.getCache());
        }
        if (semicolon) {
            ddl.append(";");
        }

        return ddl.toString();

    }

}
