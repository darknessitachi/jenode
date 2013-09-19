package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.core.handler.ZAction;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DBConnPoolImpl;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.Q;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.orm.DBImporter;
import com.zving.framework.ui.control.LongTimeTask;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.IOUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.preloader.Reloader;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

@Alias("Install")
public class InstallUI extends UIFacade {
	@Priv(login = false)
	public void init() {
		if (!Config.isInstalled) {
			if (Runtime.getRuntime().maxMemory() < 262144000L) {
				$S("LowMemory", "true");
			}
			$S("NotInstall", "true");
		}
	}

	@Priv(login=false)
  public void execute() {
//    if (Config.isInstalled) {
//      fail("已经为" + Config.getAppCode() + "初始数据库完毕，不能再次初始化!");
//      return;
//    }
//
//    final DBConnConfig dcc = new DBConnConfig();
//    dcc.isJNDIPool = "1".equals($V("isJNDIPool"));
//    dcc.isLatin1Charset = "1".equals($V("isLatin1Charset"));
//    dcc.JNDIName = $V("JNDIName");
//    dcc.DBName = $V("DBName");
//    dcc.DBPassword = $V("Password");
//    try {
//      dcc.DBPort = Integer.parseInt($V("Port"));
//    }
//    catch (NumberFormatException localNumberFormatException) {
//    }
//    dcc.DBServerAddress = $V("Address");
//    dcc.DBType = $V("ServerType");
//    dcc.DBUserName = $V("UserName");
//
//    if ((Config.isJboss()) && 
//      (dcc.JNDIName.toLowerCase().startsWith("jdbc/"))) {
//      dcc.JNDIName = dcc.JNDIName.substring(5);
//    }
//
//    DBConn conn = null;
//    try {
//      if (dcc.isMysql()) {
//        try {
//          conn = DBConnPoolImpl.createConnection(dcc, false);
//        } catch (SQLException e) {
//          e.printStackTrace();
//          dcc.DBName = "mysql";
//          try {
//            conn = DBConnPoolImpl.createConnection(dcc, false);
//
//            DataAccess da = new DataAccess(conn);
//            DataTable dt = da.executeDataTable(new Q("show variables like 'lower_case_table_names'", new Object[0]));
//            if ((dt.getRowCount() == 0) || (dt.getInt(0, 1) == 0)) {
//              fail("检查到mysql数据库区分表名大小写，请修改my.cnf或my.ini:<br><font color=red>在[mysqld]段加上一行配置lower_case_table_names=1!</font>");
//
//              conn.closeReally();
//              return;
//            }
//
//            dt = da.executeDataTable(new Q("show variables like 'character_set_database'", new Object[0]));
//            String charset = Config.getGlobalCharset().replaceAll("\\-", "");
//            if (!charset.equalsIgnoreCase(dt.getString(0, 1))) {
//              fail("检查到mysql的字符集为" + dt.getString(0, 1) + "，但程序要求的字符集为" + charset.toLowerCase() + 
//                "，请修改my.cnf或my.ini:<br><font color=red>" + "凡以default-character-set开头的行，都修改为default-character-set=" + 
//                charset.toLowerCase() + "</font>");
//              conn.closeReally();
//              return;
//            }
//            if (dcc.isJNDIPool)
//              break label924;
//            dt = da.executeDataTable(new Q("show databases like ?", new Object[] { $V("DBName") }));
//            if (dt.getRowCount() != 0) break label924;
//            LogUtil.info("安装目标数据库不存在，将自动创建目标数据库!");
//            da.executeNoQuery(new Q("create schema " + $V("DBName"), new Object[0]));
//            dcc.DBName = $V("DBName");
//            conn.close();
//            conn = DBConnPoolImpl.createConnection(dcc, false);
//          }
//          catch (Exception e2)
//          {
//            if (conn != null) {
//              conn.closeReally();
//            }
//            throw e;
//          }
//        } catch (Exception e) {
//          if (conn != null)
//            try {
//              conn.closeReally();
//            }
//            catch (Exception localException1) {
//            }
//          throw e;
//        }
//      } else if ((dcc.isSQLServer()) || (dcc.isSybase())) {
//        try {
//          conn = DBConnPoolImpl.createConnection(dcc, false);
//        } catch (SQLException e) {
//          e.printStackTrace();
//          if ((dcc.isSQLServer()) && (!dcc.isJNDIPool)) {
//            dcc.DBName = "master";
//            try {
//              conn = DBConnPoolImpl.createConnection(dcc, false);
//              DataAccess da = new DataAccess(conn);
//
//              DataTable dt = da.executeDataTable(new Q("select * from sysDatabases where name=?", new Object[] { $V("DBName") }));
//              if (dt.getRowCount() == 0) {
//                if (!Config.isSQLServer()) break label776;
//                LogUtil.info("安装目标数据库不存在，将自动创建目标数据库!");
//                da.executeNoQuery(new Q("create database " + $V("DBName"), new Object[0]));
//                dcc.DBName = $V("DBName");
//                conn.closeReally();
//                conn = DBConnPoolImpl.createConnection(dcc, false); break label776;
//              }
//
//              conn.closeReally();
//              fail("用户" + dcc.DBUserName + "没有访问数据库" + $V("DBName") + "的权限！");
//              return;
//            }
//            catch (Exception e2) {
//              throw e;
//            }
//          } else {
//            throw e;
//          }
//        } catch (Exception e) {
//          if (conn != null)
//            try {
//              conn.closeReally();
//            }
//            catch (Exception localException2) {
//            }
//          throw e;
//        }
//        label776: if ((dcc.isSybase()) && (!dcc.isJNDIPool)) {
//          DataAccess da = new DataAccess(conn);
//          try {
//            da.executeNoQuery(new Q("use master", new Object[0]));
//
//            DataTable dt = da.executeDataTable(new Q("select * from sysdatabases where name=?", new Object[] { $V("DBName") }));
//            if (dt.getRowCount() == 0) {
//              fail("安装目标数据库不存在，请手工创建!<br>注意：<br>1、注意分配给该数据库的存储空间不小于150M！<br>2、服务器页面大小必须为16K<br>3、字符集必须为UTF8且排序规则为nocase！");
//
//              conn.closeReally();
//              return;
//            }
//            da.executeNoQuery(new Q("use " + $V("DBName"), new Object[0]));
//          } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//          }
//        }
//      }
//      else {
//        conn = DBConnPoolImpl.createConnection(dcc, false);
//      }
//    
//      label924: boolean importData = "1".equals($V("ImportData"));
//      final DBConn conn2 = conn;
//      final boolean autoCreate = "1".equals($V("AutoCreate"));
//
//      if (importData) {
//        LongTimeTask ltt = LongTimeTask.getInstanceByType("Install");
//        if (ltt != null) {
//          fail("相关任务正在运行中，请先中止！");
//          return;
//        }
//        ltt = new LongTimeTask() {
//          public void execute() {
//            try {
//              DBImporter di = new DBImporter();
//              di.setTask(this);
//              Config.setValue("App.DebugMode", "true");
//              if (di.importDB(Config.getContextRealPath() + "WEB-INF/data/installer/Install.zdt", conn2, autoCreate, null)) {
//                setCurrentInfo("正在初始化系统配置");
//                InstallUI.init(conn2);
//                setPercent(33);
//                InstallUI.generateDatabaseConfig(dcc);
//                setCurrentInfo("安装完成，将重定向到登录页面!");
//                InstallUI.reload();
//              } else {
//                addError("<font color=red>导入失败，请查看服务器日志! 确认问题后请按F5刷新页面重新导入。</font>");
//              }
//            } catch (Exception e) {
//              e.printStackTrace();
//
//              if (conn2 != null)
//                try {
//                  conn2.closeReally();
//                } catch (SQLException e) {
//                  e.printStackTrace();
//                }
//            }
//            finally
//            {
//              if (conn2 != null)
//                try {
//                  conn2.closeReally();
//                } catch (SQLException e) {
//                  e.printStackTrace();
//                }
//            }
//          }
//        };
//        ltt.setType("Install");
//        ltt.setUser(User.getCurrent());
//        ltt.start();
//        $S("TaskID", ltt.getTaskID());
//        this.Response.setStatus(1);
//      } else {
//        init(conn2);
//        generateDatabaseConfig(dcc);
//        this.Response.setStatusAndMessage(2, Config.getAppCode() + "初始化完毕!");
//        reload();
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      this.Response.setStatusAndMessage(3, "连接到数据库时发生错误:" + e.getMessage());
//    }
  }

	public static void reload() {
		Thread t = new Thread() {
			public void run() {
				try {
					Reloader.isReloading = true;
					Thread.sleep(3000L);
					Reloader.reload();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.setContextClassLoader(Reloader.class.getClassLoader());
		t.start();
	}

	public static void generateDatabaseConfig(DBConnConfig dcc) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<framework>\n");
		sb.append("\t<databases>\n");
		sb.append("\t\t<database name=\"Default\">\n");
		sb.append("\t\t\t<config name=\"Type\">" + dcc.DBType + "</config>\n");
		if (dcc.isJNDIPool) {
			sb.append("\t\t\t<config name=\"JNDIName\">" + dcc.JNDIName
					+ "</config>\n");
		} else {
			sb.append("\t\t\t<config name=\"ServerAddress\">"
					+ dcc.DBServerAddress + "</config>\n");
			sb.append("\t\t\t<config name=\"Port\">" + dcc.DBPort
					+ "</config>\n");
			sb.append("\t\t\t<config name=\"Name\">" + dcc.DBName
					+ "</config>\n");
			sb.append("\t\t\t<config name=\"UserName\">" + dcc.DBUserName
					+ "</config>\n");

			String password = dcc.DBPassword;
			try {
				Class c = Class
						.forName("com.zving.framework.security.EncryptUtil");
				Method encrypt3DES = c.getMethod("encrypt3DES", new Class[] {
						String.class, String.class });
				Object defaultKey = c.getField("DEFAULT_KEY").get(null);
				Object obj = encrypt3DES.invoke(null, new Object[] {
						dcc.DBPassword, defaultKey });
				if (obj != null)
					password = "$KEY" + obj;
			} catch (Exception localException) {
			}
			sb.append("\t\t\t<config name=\"Password\">" + password
					+ "</config>\n");
			sb.append("\t\t\t<config name=\"MaxConnCount\">1000</config>\n");
			sb.append("\t\t\t<config name=\"InitConnCount\">0</config>\n");
			sb.append("\t\t\t<config name=\"TestTable\">ZDMaxNo</config>\n");
			if (dcc.isLatin1Charset) {
				sb.append("\t\t\t<config name=\"isLatin1Charset\">true</config>\n");
			}
		}
		sb.append("\t\t</database>\n");
		sb.append("\t</databases>\n");
		sb.append("\t</framework>\n");
		FileUtil.writeText(Config.getContextRealPath()
				+ "WEB-INF/plugins/classes/database.xml", sb.toString(),
				"UTF-8");
	}

	@Priv(login = false)
	@Alias(value = "platform/install/sql", alone = true)
	public void getSQL(ZAction za) {
		String dbtype = $V("Type");
		String sql = new DBImporter().getSQL(Config.getContextRealPath()
				+ "WEB-INF/data/installer/Install.zdt", dbtype);
		IOUtil.download(za.getRequest(), za.getResponse(), dbtype + ".txt",
				new ByteArrayInputStream(sql.getBytes()));
	}

	public static void init(DBConn conn) {
		try {
			if (StringUtil.isNotEmpty(Config.getContextPath()))
				ExtendManager.invoke("com.zving.platform.AfterInstall",
						new Object[] { conn });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}