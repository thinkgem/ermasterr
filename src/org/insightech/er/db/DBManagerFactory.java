package org.insightech.er.db;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.impl.access.AccessDBManager;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.h2.H2DBManager;
import org.insightech.er.db.impl.hsqldb.HSQLDBDBManager;
import org.insightech.er.db.impl.informix.InformixDBManager;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.sqlite.SQLiteDBManager;
import org.insightech.er.db.impl.sqlserver.SqlServerDBManager;
import org.insightech.er.db.impl.sqlserver2008.SqlServer2008DBManager;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.ERDiagram;

public class DBManagerFactory {

    private static final List<DBManager> DB_LIST = new ArrayList<DBManager>();

    private static final List<String> DB_ID_LIST = new ArrayList<String>();

    static {
        new MySQLDBManager();
        new OracleDBManager();
		new SqlServerDBManager();
        new PostgresDBManager();
        
        new DB2DBManager();
        new H2DBManager();
        
        new AccessDBManager();
        new HSQLDBDBManager();
        new SQLiteDBManager();
		new InformixDBManager();
		new SqlServer2008DBManager();
		
        new StandardSQLDBManager();
	}

    static void addDB(final DBManager manager) {
        DB_LIST.add(manager);
        DB_ID_LIST.add(manager.getId());
    }

    public static DBManager getDBManager(final String database) {
        for (final DBManager manager : DB_LIST) {
            if (manager.getId().equals(database)) {
                return manager;
            }
        }

        throw new IllegalArgumentException(ResourceString.getResourceString("error.database.is.not.supported") + database);
    }

    public static DBManager getDBManager(final ERDiagram diagram) {
        return getDBManager(diagram.getDatabase());
    }

    public static List<String> getAllDBList() {
        return DB_ID_LIST;
    }

}
