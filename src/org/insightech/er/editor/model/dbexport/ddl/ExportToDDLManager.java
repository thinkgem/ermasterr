package org.insightech.er.editor.model.dbexport.ddl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.command.settings.ChangeSettingsCommand;
import org.insightech.er.editor.model.dbexport.AbstractExportManager;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.io.FileUtils;

public class ExportToDDLManager extends AbstractExportManager {

    private final ExportDDLSetting exportDDLSetting;

    public ExportToDDLManager(final ExportDDLSetting exportDDLSetting) {
        super("dialog.message.export.ddl");
        this.exportDDLSetting = exportDDLSetting;
    }

    @Override
    protected int getTotalTaskCount() {
        return 2;
    }

    @Override
    protected void doProcess(final ProgressMonitor monitor) throws Exception {

        PrintWriter out = null;

        try {
        	List<String> list = exportDDLSetting.getOutputDababaseIdList();
        	if (list != null && list.size() > 0){
        		for (String database : list) {
        			DBManager dbManager = DBManagerFactory.getDBManager(database);
        			// 更改数据库类型
					Settings settings = diagram.getDiagramContents().getSettings().clone();
					settings.setDatabase(dbManager.getId());
					new ChangeSettingsCommand(diagram, settings, false).execute();
        			// 生成 DDL 文件
					String db = dbManager.getId().toLowerCase()
							.replaceAll("sqlserver", "mssql").replaceAll(" ", "");
					StringBuilder fileName = new StringBuilder(exportDDLSetting.getDdlOutput());
					int lastIndexOf = fileName.lastIndexOf(File.separator);
					if (lastIndexOf != -1){
						fileName.insert(lastIndexOf + 1, db + File.separator);
					}else{
						fileName.insert(0, db + File.separator);
					}
                    doProcess(monitor, dbManager, fileName.toString());
				}
        	}else{
        		DBManager dbManager = DBManagerFactory.getDBManager(diagram);
                doProcess(monitor, dbManager, exportDDLSetting.getDdlOutput());
        	}
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected void doProcess(final ProgressMonitor monitor, DBManager dbManager, String fileName) throws Exception {

        PrintWriter out = null;

        try {
        	
			final DDLCreator ddlCreator = dbManager.getDDLCreator(diagram, exportDDLSetting.getCategory(), true);

            ddlCreator.init(exportDDLSetting.getEnvironment(), exportDDLSetting.getDdlTarget(), exportDDLSetting.getLineFeed());

            final File file = FileUtils.getFile(projectDir, fileName);
            file.getParentFile().mkdirs();

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), exportDDLSetting.getSrcFileEncoding())));

            monitor.subTaskWithCounter("writing drop ddl");

            out.print(ddlCreator.getDropDDL(diagram));

            monitor.worked(1);

            monitor.subTaskWithCounter("writing create ddl");

            out.print(ddlCreator.getCreateDDL(diagram));

            monitor.worked(1);
        	
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    @Override
    public File getOutputFileOrDir() {
        return FileUtils.getFile(projectDir, exportDDLSetting.getDdlOutput());
    }
}
