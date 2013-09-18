package com.zving.framework;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.zving.framework.cache.CacheManager;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.schedule.CronManager;
import com.zving.preloader.PreClassLoader;

public class ContextListener implements ServletContextListener {
	private CronManager manager;

	public void contextDestroyed(ServletContextEvent arg0) {
		if (this.manager != null) {
			this.manager.destory();
		}
		CacheManager.destory();
		ExtendManager.getInstance().destory();
		cleanJdbcDriverManager();
	}

	private void cleanJdbcDriverManager() {
		List driverNames = new ArrayList();
		HashSet originalDrivers = new HashSet();
		Enumeration drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			originalDrivers.add((Driver) drivers.nextElement());
		}
		drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = (Driver) drivers.nextElement();
			if (driver.getClass().getClassLoader() == PreClassLoader
					.getInstance()) {
				if (originalDrivers.contains(driver))
					driverNames.add(driver.getClass().getCanonicalName());
				try {
					DriverManager.deregisterDriver(driver);
					System.out
							.println("Unregister JDBC driver in ContextListener success:"
									+ driver);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext sc = arg0.getServletContext();
		Config.configMap.put("System.ContainerInfo", sc.getServerInfo());
		Config.getJBossInfo();
		Config.setPluginContext(true);
		Config.loadConfig();
		ExtendManager.getInstance().start();
		this.manager = CronManager.getInstance();
	}
}