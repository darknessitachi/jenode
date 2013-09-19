package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.ResponseData;
import com.zving.framework.SessionListener;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.data.DataTable;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DBExporter;
import com.zving.framework.orm.DBImporter;
import com.zving.framework.security.LicenseInfo;
import com.zving.framework.ui.control.LongTimeTask;
import com.zving.framework.ui.control.UploadAction;
import com.zving.framework.ui.tag.ListAction;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.IOUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.PropertiesUtil;
import com.zving.platform.bl.SystemInfoBL;
import com.zving.platform.config.AppDataPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

@Alias("SystemInfo")
public class SystemInfoUI extends UIFacade
{
  @Priv
  public void initStatusName()
  {
    if (!Config.isAllowLogin)
      $S("StatusName", Lang.get("SysInfo.AllowLogin"));
    else
      $S("StatusName", Lang.get("SysInfo.DenyLogin"));
  }

  @Priv("Platform.SystemInfo.ChangeLoginStatus")
  public void changeLoginStatus()
  {
    Config.isAllowLogin = !Config.isAllowLogin;
    if (!Config.isAllowLogin)
      $S("LoginStatus", Lang.get("SysInfo.AllowLogin"));
    else
      $S("LoginStatus", Lang.get("SysInfo.DenyLogin"));
  }

  @Priv("Platform.SystemInfo.ForceExit")
  public void forceExit()
  {
    SessionListener.forceExit();
    this.Response.setStatus(1);
  }

  @Priv("Platform.SystemInfo")
  public void bindAppInfo(ListAction dla)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("Name");
    dt.insertColumn("Value");
    dt.insertRow(new Object[] { Lang.get("SysInfo.AppCode"), Config.getAppCode() });
    String title = Lang.get("Product.Name");
    dt.insertRow(new Object[] { Lang.get("SysInfo.AppName"), title == null ? Config.getAppName() : title });
    dt.insertRow(new Object[] { Lang.get("SysInfo.AppVersion"), SystemInfoBL.getAppVersion() });

    dt.insertRow(new Object[] { Lang.get("SysInfo.StartupTime"), 
      DateUtil.toString(new Date(Long.parseLong(Config.getValue("App.Uptime"))), "yyyy-MM-dd HH:mm:ss") });

    File statusFile = new File(Config.getPluginPath() + "update/update.status");
    Mapx map = statusFile.exists() ? PropertiesUtil.read(statusFile) : new Mapx();
    String lastUpdateTime = Lang.get("Platform.NeverUpdate");
    if (map.size() > 0) {
      List list = ObjectUtil.sort(map.valueArray(), new Comparator() {
        public int compare(String str1, String str2) {
          return new Long(str1).compareTo(new Long(str2));
        }
      });
      lastUpdateTime = (String)list.get(list.size() - 1);
      lastUpdateTime = DateUtil.toString(new Date(Long.parseLong(lastUpdateTime)), "yyyy-MM-dd HH:mm:ss");
    }

    dt.insertRow(new Object[] { Lang.get("SysInfo.LastUpdateTime"), lastUpdateTime });
    dt.insertRow(new Object[] { Lang.get("SysInfo.LoginCount"), new Long(Config.getLoginUserCount()) });
    dt.insertRow(new Object[] { Lang.get("SysInfo.DebugMode"), Config.getValue("App.DebugMode") });
    dla.bindData(dt);
  }

  @Priv("Platform.SystemInfo")
  public void bindSystemInfo(ListAction dla)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("Name");
    dt.insertColumn("Value");
    dt.insertRow(new Object[] { Lang.get("SysInfo.OSName"), Config.getValue("System.OSName") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.OSVersion"), Config.getValue("System.OSVersion") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.OSPatch"), Config.getValue("System.OSPatchLevel") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.JDKVendor"), Config.getValue("System.JavaVendor") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.JDKVersion"), Config.getValue("System.JavaVersion") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.JDKHome"), Config.getValue("System.JavaHome") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.ContainerName"), Config.getValue("System.ContainerInfo") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.ContainerUser"), Config.getValue("System.OSUserName") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.MemoryInfo"), 
      Runtime.getRuntime().totalMemory() / 1024L / 1024L + "M/" + Runtime.getRuntime().maxMemory() / 1024L / 1024L + "M" });
    dt.insertRow(new Object[] { Lang.get("SysInfo.FileEncoding"), Config.getFileEncode() });
    dla.bindData(dt);
  }

  @Priv("Platform.SystemInfo")
  public void bindDBInfo(ListAction dla)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("Name");
    dt.insertColumn("Value");
    DBConnConfig dcc = DBConnPool.getDBConnConfig();
    dt.insertRow(new Object[] { Lang.get("SysInfo.DBType"), dcc.DBType });
    if (dcc.isJNDIPool) {
      dt.insertRow(new Object[] { Lang.get("SysInfo.JNDIName"), dcc.JNDIName });
    } else {
      dt.insertRow(new Object[] { Lang.get("SysInfo.DBAddress"), dcc.DBServerAddress });
      dt.insertRow(new Object[] { Lang.get("SysInfo.DBPort"), dcc.DBPort });
      dt.insertRow(new Object[] { Lang.get("SysInfo.DBName"), dcc.DBName });
      dt.insertRow(new Object[] { Lang.get("SysInfo.DBUser"), dcc.DBUserName });
    }
    dla.bindData(dt);
  }

  @Priv("Platform.SystemInfo")
  public void bindLicenseInfo(ListAction dla)
  {
    DataTable dt = new DataTable();
    dt.insertColumn("Name");
    dt.insertColumn("Value");
    dt.insertRow(new Object[] { Lang.get("SysInfo.LicenseTo"), LicenseInfo.getName() });
    dt.insertRow(new Object[] { Lang.get("SysInfo.ValidEndDate"), 
      DateUtil.toString(LicenseInfo.getEndDate(), "yyyy-MM-dd HH:mm:ss") });
    dt.insertRow(new Object[] { Lang.get("SysInfo.LicensedUserCount"), new Long(LicenseInfo.getUserLimit()) });
    dt.insertRow(new Object[] { Lang.get("SysInfo.LicensedProduct"), Config.getAppCode() });
    dt.insertRow(new Object[] { Lang.get("SysInfo.LicensedMac"), LicenseInfo.getMacAddress() });
    dla.bindData(dt);
  }

  @Priv("Platform.SystemInfo.Export")
  public void exportDB()
  {
    LongTimeTask ltt = LongTimeTask.getInstanceByType("ExportDB");
    if ((ltt != null) && (ltt.isAlive())) {
      fail("Platform.DBExportTaskRunning");
      return;
    }
    String fileName = "DB_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".zdt";
    final String path = AppDataPath.getValue() + "backup/" + fileName;
    ltt = new LongTimeTask() {
      public void execute() {
        long start = System.currentTimeMillis();

        DBExporter dbExporter = new DBExporter();
        dbExporter.setTask(this);
        dbExporter.exportDB(path);

        LogUtil.info("导出数据库完成，耗时(s)：" + (System.currentTimeMillis() - start) / 1000L + " 。");
      }
    };
    ltt.setType("ExportDB");
    ltt.setUser(User.getCurrent());
    ltt.start();
    $S("TaskID", Long.valueOf(ltt.getTaskID()));
    $S("FileName", fileName);
  }
  @Priv("Platform.SystemInfo.Export")
  @Alias(value="platform/systeminfo/download", alone=true)
  public void downloadDB(ZAction za) {
    String fileName = $V("FileName");
    String path = AppDataPath.getValue() + "backup/" + fileName;
    path = FileUtil.normalizePath(path);
    fileName = path.substring(path.lastIndexOf("/") + 1);
    try {
      if (!FileUtil.exists(path)) {
        za.writeHTML("<script>alert('" + Lang.get("Platform.SystemInfo.DBExport.FileNotExisted") + "')</script>");
        return;
      }
      FileInputStream fis = new FileInputStream(path);
      IOUtil.download(za.getRequest(), za.getResponse(), fileName, fis);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Priv("Platform.SystemInfo.Import")
  public void uploadDB(UploadAction ua)
  {
    final String FileName = AppDataPath.getValue() + "backup/DBUpload_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + ".zdt";
    try {
      if (!FileUtil.exists(AppDataPath.getValue() + "backup")) {
        FileUtil.mkdir(AppDataPath.getValue() + "backup");
      }
      ua.getFirstFile().write(new File(FileName));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    LongTimeTask ltt = LongTimeTask.getInstanceByType("Install");
    if (ltt != null) {
      fail(Lang.get("SysInfo.Installing"));
      return;
    }
    SessionListener.forceExit();
    Config.isAllowLogin = false;
    ltt = new LongTimeTask() {
      public void execute() {
        DBImporter di = new DBImporter();
        di.setTask(this);
        di.importDB(FileName);
        setPercent(100);
        InstallUI.reload();
      }
    };
    ltt.setType("Install");
    ltt.setUser(User.getCurrent());
    ltt.start();
    Config.isAllowLogin = true;
    $S("TaskID", Long.valueOf(ltt.getTaskID()));
  }
}