package com.zving.framework.data.nativejdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class WebSphereNativeJdbcExtractor
{
  private static final String JDBC_ADAPTER_CONNECTION_NAME_5 = "com.ibm.ws.rsadapter.jdbc.WSJdbcConnection";
  private static final String JDBC_ADAPTER_UTIL_NAME_5 = "com.ibm.ws.rsadapter.jdbc.WSJdbcUtil";
  private static Class<?> webSphere5ConnectionClass;
  private static Method webSphere5NativeConnectionMethod;

  public static void init()
  {
    try
    {
      webSphere5ConnectionClass = WebSphereNativeJdbcExtractor.class.getClassLoader().loadClass(
        "com.ibm.ws.rsadapter.jdbc.WSJdbcConnection");
      Class jdbcAdapterUtilClass = WebSphereNativeJdbcExtractor.class.getClassLoader().loadClass(
        "com.ibm.ws.rsadapter.jdbc.WSJdbcUtil");
      webSphere5NativeConnectionMethod = jdbcAdapterUtilClass.getMethod("getNativeConnection", 
        new Class[] { webSphere5ConnectionClass });
    } catch (Exception ex) {
      throw new IllegalStateException(
        "Could not initialize WebSphereNativeJdbcExtractor because WebSphere API classes are not available: " + 
        ex);
    }
  }

  public static Connection doGetNativeConnection(Connection con)
    throws SQLException
  {
    if (webSphere5ConnectionClass == null) {
      init();
    }
    if (webSphere5ConnectionClass.isAssignableFrom(con.getClass())) {
      try {
        return (Connection)webSphere5NativeConnectionMethod.invoke(null, new Object[] { con });
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return con;
  }
}