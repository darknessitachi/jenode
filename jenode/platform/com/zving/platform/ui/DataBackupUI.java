package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.SessionListener;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.data.DataTable;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DBExporter;
import com.zving.framework.orm.DBImporter;
import com.zving.framework.ui.control.DataGridAction;
import com.zving.framework.ui.control.LongTimeTask;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.IOUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

@Alias("DataBackup")
public class DataBackupUI extends UIFacade
{
  @Priv
  public void bindGrid(DataGridAction dga)
  {
    DataTable dt = new DataTable();
    dt.insertColumns(new String[] { "FileName", "FileSize", "LastModifyTime" });

    File backupDir = new File(Config.getContextRealPath() + "WEB-INF/backup/");
    FileUtil.mkdir(backupDir.getAbsolutePath());
    File[] files = backupDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        if (name.endsWith(".zdt")) {
          return true;
        }
        return false;
      }
    });
    for (File f : files) {
      Date d = new Date(f.lastModified());
      long length = f.length() / 1024L;
      if (f.length() % 1024L != 0L) {
        length += 1L;
      }
      DecimalFormat df = new DecimalFormat("#,###");
      String size = df.format(length) + " KB";
      dt.insertRow(new Object[] { f.getName(), size, DateUtil.toDateTimeString(d) });
    }

    dga.bindData(dt);
  }

  @Priv
  public void backup()
  {
    LongTimeTask ltt = LongTimeTask.getInstanceByType("DataBackup");
    if ((ltt != null) && (ltt.isAlive())) {
      fail(Lang.get("Platform.DataBackupTaskRunning"));
      return;
    }
    String fileName = "DataBackup_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".zdt";
    final String path = Config.getContextRealPath() + "WEB-INF/backup/" + fileName;
    ltt = new LongTimeTask() {
      public void execute() {
        long start = System.currentTimeMillis();

        DBExporter dbExporter = new DBExporter();
        dbExporter.setTask(this);
        dbExporter.exportDB(path);

        LogUtil.info("备份数据库完成，耗时(s)：" + (System.currentTimeMillis() - start) / 1000L + " 。");
      }
    };
    ltt.setType("DataBackup");
    ltt.setUser(User.getCurrent());
    ltt.start();
    $S("TaskID", Long.valueOf(ltt.getTaskID()));
    $S("Path", path);
  }

  @Priv
  public void reset() {
    String fileName = $V("FileName");
    final String filePath = Config.getContextRealPath() + "WEB-INF/backup/" + fileName;
    if (!FileUtil.exists(filePath)) {
      fail("备份文件不存在：" + filePath);
      return;
    }
    LongTimeTask ltt = LongTimeTask.getInstanceByType("ResetDB");
    if (ltt != null) {
      fail("恢复数据任务正在进行中！");
      return;
    }
    SessionListener.forceExit();
    Config.isAllowLogin = false;
    ltt = new LongTimeTask() {
      public void execute() {
        DBImporter di = new DBImporter();
        di.setTask(this);
        di.importDB(filePath);
        setPercent(100);
        InstallUI.reload();
      }
    };
    ltt.setType("ResetDB");
    ltt.setUser(User.getCurrent());
    ltt.start();
    Config.isAllowLogin = true;
    $S("TaskID", Long.valueOf(ltt.getTaskID()));
  }

  @Priv
  public void del() {
    String fileNames = $V("FileNames");
    String[] arr = StringUtil.splitEx(fileNames, ",");
    for (String fileName : arr) {
      String filePath = Config.getContextRealPath() + "WEB-INF/backup/" + fileName;
      FileUtil.delete(filePath);
    }
    success(Lang.get("Common.DeleteSuccess"));
  }
  @Priv
  @Alias(value="platform/databackup/download", alone=true)
  public void download(ZAction za) {
    String fileName = $V("FileName");
    String filePath = Config.getContextRealPath() + "WEB-INF/backup/" + fileName;
    filePath = FileUtil.normalizePath(filePath);
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filePath);
      IOUtil.download(za.getRequest(), za.getResponse(), fileName, fis);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      za.writeHTML("File not found: " + fileName);
      try
      {
        if (fis != null)
          fis.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    finally
    {
      try
      {
        if (fis != null)
          fis.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}