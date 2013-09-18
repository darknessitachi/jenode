package com.zving.framework;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.xml.XMLElement;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

public class Config {
	protected static Mapx<String, String> configMap = new Mapx();

	public static int OnlineUserCount = 0;

	public static int LoginUserCount = 0;

	public static boolean isInstalled = false;

	public static boolean isAllowLogin = true;

	public static boolean isPluginContext = false;

	private static String AppCode = null;

	private static String AppName = null;

	static boolean ComplexDepolyMode = false;

	static boolean FrontDeploy = false;
	public static int ServletMajorVersion;
	public static int ServletMinorVersion;
	protected static String GlobalCharset;
	private static String ClassPath;
	private static Boolean debugModeFlag;

	protected static void init() {
		if (!configMap.containsKey("System.JavaVersion")) {
			ConfigLoader.load();

			configMap.put("App.ContextRealPath", getContextRealPath());
			configMap.put("System.JavaVersion",
					System.getProperty("java.version"));
			configMap.put("System.JavaVendor",
					System.getProperty("java.vendor"));
			configMap.put("System.JavaHome", System.getProperty("java.home"));
			configMap.put("System.OSPatchLevel",
					System.getProperty("sun.os.patch.level"));
			configMap.put("System.OSArch", System.getProperty("os.arch"));
			configMap.put("System.OSVersion", System.getProperty("os.version"));
			configMap.put("System.OSName", System.getProperty("os.name"));
			if ((System.getProperty("os.name").toLowerCase().indexOf("windows") > 0)
					&& (System.getProperty("os.name").equals("6.1"))) {
				configMap.put("System.OSName", "Windows 7");
			}
			configMap.put("System.OSUserLanguage",
					System.getProperty("user.language"));
			configMap.put("System.OSUserName", System.getProperty("user.name"));
			configMap.put("System.LineSeparator",
					System.getProperty("line.separator"));
			configMap.put("System.FileSeparator",
					System.getProperty("file.separator"));
			configMap.put("System.FileEncoding",
					System.getProperty("file.encoding"));

			List<XMLElement> datas = ConfigLoader
					.getElements("framework.application.config");
			if (datas == null) {
				LogUtil.warn("File framework.xml not found");
				isInstalled = false;
				return;
			}
			for (XMLElement data : datas) {
				configMap.put("App."
						+ (String) data.getAttributes().get("name"),
						data.getText());
			}
			datas = ConfigLoader.getElements("zcms.allowUploadExt.config");
			for (XMLElement data : datas) {
				configMap.put((String) data.getAttributes().get("name"),
						data.getText());
			}
			datas = ConfigLoader.getElements("data.config");
			for (XMLElement data : datas) {
				configMap.put((String) data.getAttributes().get("name"),
						(String) data.getAttributes().get("value"));
			}
			ComplexDepolyMode = "true".equals(configMap
					.getString("App.ComplexDepolyMode"));
			FrontDeploy = "true".equals(configMap.getString("App.FrontDeploy"));

			datas = ConfigLoader.getElements("framework.databases.database");
			List<XMLElement> children;

			for (XMLElement data : datas) {
				String dbname = (String) data.getAttributes().get("name");
				children = data.elements();

				for (XMLElement child : children) {
					String attr = (String) (child).getAttributes().get("name");
					String value = ((XMLElement) child).getText();
					if ((attr.equalsIgnoreCase("Password"))
							&& (value.startsWith("$KEY"))) {
						try {
							Class c = Class
									.forName("com.zving.framework.security.EncryptUtil");
							if (c != null) {
								Method decrypt3DES = c.getMethod("decrypt3DES",
										new Class[] { String.class,
												String.class });
								Object defaultKey = c.getField("DEFAULT_KEY")
										.get(null);
								Object obj = decrypt3DES.invoke(null,
										new Object[] { value.substring(4),
												defaultKey });
								if (obj != null)
									value = obj.toString();
							}
						} catch (Exception localException) {
						}
					}
					configMap.put("Database." + dbname + "." + attr, value);

				}
			}

			if (datas.size() > 0)
				isInstalled = true;
			else {
				isInstalled = false;
			}
			LogUtil.info("----" + getAppCode() + "(" + getAppName()
					+ "): Config Initialized----");
		}
	}

	public static void loadConfig() {
		configMap.remove("System.JavaVersion");
		ConfigLoader.reload();
		init();
	}

	public static Mapx<String, String> getMapx() {
		return configMap;
	}

	public static boolean isFrontDeploy() {
		return FrontDeploy;
	}

	public static String getValue(String configName) {
		init();
		return (String) configMap.get(configName);
	}

	public static void setValue(String configName, String configValue) {
		init();
		configMap.put(configName, configValue);
	}

	public static String getWEBINFPath() {
		String path = getClassesPath();
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(0, path.lastIndexOf("WEB-INF") + 8);
		}
		return path;
	}

	public static String getClassesPath() {
		if (ClassPath == null) {
			URL url = Config.class.getClassLoader().getResource(
					"com/zving/framework/Config.class");
			if (url == null) {
				System.err.println("Config.getClassesPath() failed!");
				return "";
			}
			try {
				String path = URLDecoder.decode(url.getPath(), getFileEncode());
				if ((System.getProperty("os.name").toLowerCase()
						.indexOf("windows") >= 0)
						&& (path.startsWith("/"))) {
					path = path.substring(1);
				}

				if (path.startsWith("file:/"))
					path = path.substring(6);
				else if (path.startsWith("jar:file:/")) {
					path = path.substring(10);
				}
				if (path.indexOf(".jar!") > 0) {
					path = path.substring(0, path.indexOf(".jar!"));
				}
				path = path.replace('\\', '/');
				path = path.substring(0, path.lastIndexOf("/") + 1);
				if (path.indexOf("WEB-INF") >= 0) {
					path = path.substring(0, path.lastIndexOf("WEB-INF") + 7)
							+ "/plugins/classes/";
				}
				if ((System.getProperty("os.name").toLowerCase()
						.indexOf("windows") < 0)
						&& (!path.startsWith("/"))) {
					path = "/" + path;
				}

				ClassPath = path;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return ClassPath;
	}

	public static String getPluginPath() {
		File f = new File(getClassesPath());
		String path = f.getParentFile().getAbsolutePath() + "/";
		return path;
	}

	public static String getContextRealPath() {
		if (configMap != null) {
			String str = (String) configMap.get("App.ContextRealPath");
			if (str != null) {
				return str;
			}
		}
		String path = getClassesPath();
		int index = path.indexOf("WEB-INF");
		if (index > 0) {
			path = path.substring(0, index);
		}
		return path;
	}

	public static String getContextPath() {
		if (ComplexDepolyMode) {
			String path = (String) User.getValue("App.ContextPath");
			if (StringUtil.isEmpty(path)) {
				path = getValue("App.ContextPath");
			}
			return path;
		}
		return getValue("App.ContextPath");
	}

	private static void initProduct() {
		if (AppCode == null) {
			if (configMap.get("App.Code") != null) {
				AppCode = configMap.getString("App.Code");
				AppName = configMap.getString("App.Name");
			}
			if (AppCode == null) {
				AppCode = "ZPlatform";
				AppName = "Zving Business Platform";
			}
		}
	}

	public static String getAppCode() {
		initProduct();
		return AppCode;
	}

	public static String getAppName() {
		initProduct();
		return AppName;
	}

	public static boolean isDebugMode() {
		if (debugModeFlag == null) {
			debugModeFlag = Boolean.valueOf("true"
					.equalsIgnoreCase(getValue("App.DebugMode")));
		}
		return debugModeFlag.booleanValue();
	}

	public static String getJavaVersion() {
		return getValue("System.JavaVersion");
	}

	public static String getJavaVendor() {
		return getValue("System.JavaVendor");
	}

	public static String getJavaHome() {
		return getValue("System.JavaHome");
	}

	public static String getContainerInfo() {
		return getValue("System.ContainerInfo");
	}

	public static String getContainerVersion() {
		String str = getValue("System.ContainerInfo");
		if (str.indexOf("/") > 0) {
			return str.substring(str.lastIndexOf("/") + 1);
		}
		return "0";
	}

	public static String getOSName() {
		return getValue("System.OSName");
	}

	public static String getOSPatchLevel() {
		return getValue("System.OSPatchLevel");
	}

	public static String getOSArch() {
		return getValue("System.OSArch");
	}

	public static String getOSVersion() {
		return getValue("System.OSVersion");
	}

	public static String getOSUserLanguage() {
		return getValue("System.OSUserLanguage");
	}

	public static String getOSUserName() {
		return getValue("System.OSUserName");
	}

	public static String getLineSeparator() {
		return getValue("System.LineSeparator");
	}

	public static String getFileSeparator() {
		return getValue("System.FileSeparator");
	}

	public static String getFileEncode() {
		return System.getProperty("file.encoding");
	}

	public static int getLoginUserCount() {
		return LoginUserCount;
	}

	public static int getOnlineUserCount() {
		return OnlineUserCount;
	}

	public static boolean isDB2() {
		return DBConnPool.getDBConnConfig().DBType.equals("DB2");
	}

	public static boolean isOracle() {
		return DBConnPool.getDBConnConfig().DBType.equals("ORACLE");
	}

	public static boolean isMysql() {
		return DBConnPool.getDBConnConfig().DBType.equals("MYSQL");
	}

	public static boolean isSQLServer() {
		return DBConnPool.getDBConnConfig().DBType.equals("MSSQL");
	}

	public static boolean isSybase() {
		return DBConnPool.getDBConnConfig().DBType.equals("SYBASE");
	}

	public static boolean isTomcat() {
		if (StringUtil.isEmpty(getContainerInfo())) {
			getJBossInfo();
		}
		return getContainerInfo().toLowerCase().indexOf("tomcat") >= 0;
	}

	public static void getJBossInfo() {
		String jboss = System.getProperty("jboss.home.dir");
		if (StringUtil.isNotEmpty(jboss))
			try {
				Class c = Class.forName("org.jboss.Version");
				Method m = c.getMethod("getInstance", null);
				Object o = m.invoke(null, null);
				m = c.getMethod("getMajor", null);
				Object major = m.invoke(o, null);
				m = c.getMethod("getMinor", null);
				Object minor = m.invoke(o, null);
				m = c.getMethod("getRevision", null);
				Object revision = m.invoke(o, null);
				m = c.getMethod("getTag", null);
				Object tag = m.invoke(o, null);
				configMap.put("System.ContainerInfo", "JBoss/" + major + "."
						+ minor + "." + revision + "." + tag);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static boolean isJboss() {
		if (StringUtil.isEmpty(getContainerInfo())) {
			getJBossInfo();
		}
		return getContainerInfo().toLowerCase().indexOf("jboss") >= 0;
	}

	public static boolean isWeblogic() {
		return getContainerInfo().toLowerCase().indexOf("weblogic") >= 0;
	}

	public static boolean isWebSphere() {
		return getContainerInfo().toLowerCase().indexOf("websphere") >= 0;
	}

	public static boolean isComplexDepolyMode() {
		return ComplexDepolyMode;
	}

	public static String getLoginPage() {
		String str = configMap.getString("App.LoginPage");
		if (StringUtil.isNotEmpty(str)) {
			return str;
		}
		return "login.zhtml";
	}

	public static String getGlobalCharset() {
		if (GlobalCharset == null) {
			ConfigLoader.load();
		}
		if (GlobalCharset == null) {
			GlobalCharset = "UTF-8";
		}
		return GlobalCharset;
	}

	public static boolean isPluginContext() {
		return isPluginContext;
	}

	public static void setPluginContext(boolean isPluginContext) {
		isPluginContext = isPluginContext;
	}
}