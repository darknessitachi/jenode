package com.zving.framework.data.nativejdbc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;

public class CommonsDbcpNativeJdbcExtractor
{
  private static final String GET_INNERMOST_DELEGATE_METHOD_NAME = "getInnermostDelegate";

  private static Object getInnermostDelegate(Object obj)
    throws SQLException
  {
    if (obj == null)
      return null;
    try
    {
      Class classToAnalyze = obj.getClass();
      while (!Modifier.isPublic(classToAnalyze.getModifiers())) {
        classToAnalyze = classToAnalyze.getSuperclass();
        if (classToAnalyze == null)
        {
          return obj;
        }
      }
      Method getInnermostDelegate = classToAnalyze.getMethod("getInnermostDelegate", null);
      Object delegate = getInnermostDelegate.invoke(obj, new Object[0]);
      return delegate != null ? delegate : obj;
    } catch (SecurityException ex) {
      throw new IllegalStateException("Commons DBCP getInnermostDelegate method is not accessible: " + ex);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obj;
  }

  public static Connection doGetNativeConnection(Connection con) throws SQLException {
    return (Connection)getInnermostDelegate(con);
  }
}