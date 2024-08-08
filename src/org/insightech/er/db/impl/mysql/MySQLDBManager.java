package org.insightech.er.db.impl.mysql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class MySQLDBManager extends DBManagerBase {

    public static final String ID = "MySQL";

    private static final ResourceBundle CHARACTER_SET_RESOURCE = ResourceBundle.getBundle("mysql_characterset");

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
//        return "com.mysql.jdbc.Driver";
    	return "com.mysql.cj.jdbc.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getURL() {
        return "jdbc:mysql://<SERVER NAME>:<PORT>/<DB NAME>";
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public SqlTypeManager getSqlTypeManager() {
        return new MySQLSqlTypeManager();
    }

    @Override
    public TableProperties createTableProperties(final TableProperties tableProperties) {
        if (tableProperties != null && tableProperties instanceof MySQLTableProperties) {
            return tableProperties;
        }

        return new MySQLTableProperties();
    }

    @Override
    public DDLCreator getDDLCreator(final ERDiagram diagram, final Category targetCategory, final boolean semicolon) {
        return new MySQLDDLCreator(diagram, targetCategory, semicolon);
    }

    @Override
    public List<String> getIndexTypeList(final ERTable table) {
        final List<String> list = new ArrayList<String>();

        list.add("BTREE");

        return list;
    }

    @Override
    protected int[] getSupportItems() {
        return new int[] {SUPPORT_AUTO_INCREMENT, SUPPORT_AUTO_INCREMENT_SETTING, SUPPORT_DESC_INDEX, SUPPORT_FULLTEXT_INDEX, SUPPORT_SCHEMA};
    }

    @Override
    public ImportFromDBManager getTableImportManager() {
        return new MySQLTableImportManager();
    }

    @Override
    public PreImportFromDBManager getPreTableImportManager() {
        return new MySQLPreTableImportManager();
    }

    @Override
    public PreTableExportManager getPreTableExportManager() {
        return new MySQLPreTableExportManager();
    }

    @Override
    public TablespaceProperties createTablespaceProperties() {
        return new MySQLTablespaceProperties();
    }

    @Override
    public TablespaceProperties checkTablespaceProperties(final TablespaceProperties tablespaceProperties) {

        if (!(tablespaceProperties instanceof MySQLTablespaceProperties)) {
            return new MySQLTablespaceProperties();
        }

        return tablespaceProperties;
    }

    @Override
    public String[] getCurrentTimeValue() {
        return new String[] {"NOW(), SYSDATE()"};
    }

    @Override
    public BigDecimal getSequenceMaxValue() {
        return null;
    }

    public static List<String> getCharacterSetList() {
        final List<String> list = new ArrayList<String>();

        final Enumeration<String> keys = CHARACTER_SET_RESOURCE.getKeys();

        while (keys.hasMoreElements()) {
            list.add(keys.nextElement());
        }

        return list;
    }

    public static List<String> getCollationList(final String characterset) {
        final List<String> list = new ArrayList<String>();

        if (characterset != null) {
            try {
                final String values = CHARACTER_SET_RESOURCE.getString(characterset);

                if (values != null) {
                    final StringTokenizer tokenizer = new StringTokenizer(values, ",");

                    while (tokenizer.hasMoreElements()) {
                        final String token = tokenizer.nextToken().trim();
                        list.add(token);
                    }
                }
            } catch (final MissingResourceException e) {}
        }

        return list;
    }
}
