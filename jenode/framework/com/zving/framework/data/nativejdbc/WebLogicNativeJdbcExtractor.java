package com.zving.framework.data.nativejdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class WebLogicNativeJdbcExtractor
{
  private static final String JDBC_EXTENSION_NAME = "weblogic.jdbc.extensions.WLConnection";
  private static Class<?> jdbcExtensionClass;
  private static Method getVendorConnectionMethod;

  public static void init()
  {
    try
    {
      jdbcExtensionClass = WebLogicNativeJdbcExtractor.class.getClassLoader().loadClass("weblogic.jdbc.extensions.WLConnection");
      getVendorConnectionMethod = jdbcExtensionClass.getMethod("getVendorConnection", null);
    } catch (Exception ex) {
      throw new IllegalStateException(
        "Could not initialize WebLogicNativeJdbcExtractor because WebLogic API classes are not available: " + 
        ex);
    }
  }

  public static Connection doGetNativeConnection(Connection con)
    throws SQLException
  {
    if (jdbcExtensionClass == null) {
      init();
    }
    if (jdbcExtensionClass.isAssignableFrom(con.getClass())) {
      try {
        return (Connection)getVendorConnectionMethod.invoke(con, new Object[0]);
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