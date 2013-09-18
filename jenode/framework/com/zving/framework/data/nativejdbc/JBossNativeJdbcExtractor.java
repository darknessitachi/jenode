package com.zving.framework.data.nativejdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class JBossNativeJdbcExtractor
{
  private static final String WRAPPED_CONNECTION_NAME = "org.jboss.resource.adapter.jdbc.WrappedConnection";
  private static Class<?> wrappedConnectionClass;
  private static Method getUnderlyingConnectionMethod;

  public static void init()
  {
    try
    {
      wrappedConnectionClass = JBossNativeJdbcExtractor.class.getClassLoader().loadClass("org.jboss.resource.adapter.jdbc.WrappedConnection");
      getUnderlyingConnectionMethod = wrappedConnectionClass.getMethod("getUnderlyingConnection", null);
    } catch (Exception ex) {
      throw new IllegalStateException(
        "Could not initialize JBossNativeJdbcExtractor because JBoss API classes are not available: " + ex);
    }
  }

  public static Connection doGetNativeConnection(Connection con)
    throws SQLException
  {
    if (wrappedConnectionClass == null) {
      init();
    }
    if (wrappedConnectionClass.isAssignableFrom(con.getClass())) {
      try {
        return (Connection)getUnderlyingConnectionMethod.invoke(con, new Object[0]);
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