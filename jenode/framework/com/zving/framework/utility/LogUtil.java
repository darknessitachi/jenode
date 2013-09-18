package com.zving.framework.utility;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.schedule.CronMonitor;
import com.zving.preloader.facade.ErrorPrintStreamFacade;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {
	private static boolean initFlag = false;

	private static Log cron = null;

	private static Log console = null;

	private static Log error = null;

	private static LogAppender appender = new LogAppender();

	private static Properties ps = new Properties();

	public static void init() {
		PrintStream syserr = System.err;
		try {
			if (ObjectUtil.empty(Config.getPluginPath())) {
				System.err.println("LogUtil cann't get plugins path.");
			} else {
				ErrorPrintStreamFacade errStream = new ErrorPrintStreamFacade(
						System.err);
				Log4jErrorPrintStream logStream = new Log4jErrorPrintStream(
						System.err);
				errStream.setPrintStream(logStream);
				System.setErr(errStream);
				String fileName = Config.getPluginPath()
						+ "classes/log4j.config";
				String txt = null;
				if (new File(fileName).exists()) {
					txt = FileUtil.readText(fileName);
				}
				if (StringUtil.isNotEmpty(txt)) {
					txt = StringUtil.replaceEx(txt, "%{ContextRealPath}",
							Config.getContextRealPath());
					 Mapx<String, String> map = StringUtil.splitToMapx(txt, "\n", "=");
					for (String key : map.keyArray())
						if ((!StringUtil.isEmpty(key))
								&& (!key.startsWith("#"))) {
							ps.put(key, ((String) map.get(key)).trim());
						}
					PropertyConfigurator.configure(ps);
					cron = LogFactory.getLog("cronLogger");
					console = LogFactory.getLog("consoleLogger");
					error = LogFactory.getLog("errorLogger");
				} else {
					System.setErr(syserr);
					System.err.println("Cann't load log4j.config.");
				}
			}
		} catch (Exception e) {
			System.setErr(syserr);
			e.printStackTrace();
		}
	}

	private static Log getLogger() {
		if (!initFlag) {
			init();
			initFlag = true;
		}
		if (CronMonitor.isCronThread()) {
			return cron;
		}
		return console;
	}

	public static void info(Object obj) {
		Log log = getLogger();
		if (log == null) {
			System.out.println(obj);
			return;
		}
		log.info(Config.getAppCode() + " " + obj);
		appender.add("INFO: " + DateUtil.getCurrentDateTime() + " "
				+ Config.getAppCode() + " " + obj);
	}

	public static void debug(Object obj) {
		Log log = getLogger();
		if (log == null) {
			System.out.println(obj);
			return;
		}
		log.debug(Config.getAppCode() + " " + obj);
		appender.add("DEBUG: " + DateUtil.getCurrentDateTime() + " "
				+ Config.getAppCode() + " " + obj);
	}

	public static void warn(Object obj) {
		Log log = getLogger();
		if (log == null) {
			System.err.println(obj);
			return;
		}
		log.warn(Config.getAppCode() + " " + obj);
		appender.add("WARN: " + DateUtil.getCurrentDateTime() + " "
				+ Config.getAppCode() + " " + obj);
	}

	public static void error(Object obj) {
		Log log = getLogger();
		if (log == null) {
			System.err.println(obj);
			return;
		}
		log.error(Config.getAppCode() + " " + obj);
		appender.add("ERROR: " + DateUtil.getCurrentDateTime() + " "
				+ Config.getAppCode() + " " + obj);
	}

	public static void fatal(Object obj) {
		Log log = getLogger();
		if (log == null) {
			System.err.println(obj);
			return;
		}
		log.fatal(Config.getAppCode() + " " + obj);
		appender.add("FATAL: " + DateUtil.getCurrentDateTime() + " "
				+ Config.getAppCode() + " " + obj);
	}

	static class Log4jErrorPrintStream extends PrintStream {
		boolean startFlag = false;

		Log4jErrorPrintStream(OutputStream out) {
			super(out);
		}

		public synchronized void println(String obj) {
			println(obj);
		}

		public synchronized void println(Object obj) {
			if (this.startFlag) {
				return;
			}
			this.startFlag = true;
			try {
				if (LogUtil.error != null) {
					LogUtil.error.error(obj);
					LogUtil.appender.add("ERROR: "
							+ DateUtil.getCurrentDateTime() + " " + obj);
				}
			} catch (Throwable e) {
				System.out
						.println("LogUtil.Log4jErrorPrintStream.println() failed:"
								+ e.getMessage());
			} finally {
				this.startFlag = false;
			}
		}
	}
}