package com.zving.platform.service;

import com.zving.framework.Config;
import com.zving.framework.orm.DBExporter;
import com.zving.framework.schedule.SystemTask;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import java.io.File;
import java.io.FilenameFilter;

public class DBBackupTask extends SystemTask
{
  public static final String ID = "com.zving.platform.service.DBBackupTask";

  public String getID()
  {
    return "com.zving.platform.service.DBBackupTask";
  }

  public String getName() {
    return "@{Platform.DBBackupTask}";
  }

  public void execute()
  {
    long start = System.currentTimeMillis();

    String backupDir = Config.getContextRealPath() + "WEB-INF/backup/";
    File[] files = new File(backupDir).listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        if (name.endsWith(".zdt")) {
          return true;
        }
        return false;
      }
    });
    if (files != null) {
      for (File file : files)
      {
        if (start - file.lastModified() > -1702967296L) {
          FileUtil.delete(file);
        }
      }
    }
    String fileName = "DataBackupTask_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".zdt";
    DBExporter dbExporter = new DBExporter();
    dbExporter.exportDB(backupDir + fileName);

    LogUtil.info("定时备份数据库完成，耗时(s)：" + (System.currentTimeMillis() - start) / 1000L + " 。");
  }

  public static void main(String[] args)
  {
  }

  public String getDefaultCronExpression()
  {
    return "0 4 * * *";
  }
}