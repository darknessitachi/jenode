package com.zving.framework.data;

import com.zving.framework.collection.Mapx;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBUtil
{
  public static String clobToString(Clob clob)
  {
    if (clob == null)
      return null;
    try
    {
      Reader r = clob.getCharacterStream();
      StringWriter sw = new StringWriter();
      char[] cs = new char[(int)clob.length()];
      try {
        r.read(cs);
        sw.write(cs);
        return sw.toString();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] blobToBytes(Blob blob) {
    if (blob == null)
      return null;
    try
    {
      return blob.getBytes(1L, (int)blob.length());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static DataTable getTableInfo()
  {
    return getTableInfo(DBConnPool.getDBConnConfig());
  }

  public static DataTable getTableInfo(DBConnConfig dcc) {
    Connection conn = null;
    try {
      conn = DBConnPoolImpl.createConnection(dcc, false);
      DatabaseMetaData dbm = conn.getMetaData();
      String currentCatalog = conn.getCatalog();
      ResultSet rs = dbm.getTables(currentCatalog, null, null, null);
      DataTable dt = new DataTable(rs);
      for (DataRow dr : dt) {
        if (dr.get(1) != null) {
          dr.set(2, dr.getString(1) + "." + dr.getString(2));
        }
      }
      return dt;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public static DataTable getColumnInfo(String tableName)
  {
    return getColumnInfo(DBConnPool.getDBConnConfig(), tableName);
  }

  public static DataTable getColumnInfo(DBConnConfig dcc, String tableName)
  {
    DBConn conn = null;
    try {
      conn = DBConnPoolImpl.createConnection(dcc, false);
      DatabaseMetaData dbm = conn.getMetaData();
      String currentCatalog = conn.getCatalog();
      String schema = null;
      String oldName = tableName;
      int index = tableName.indexOf(".");
      if (index > 0) {
        schema = tableName.substring(0, index);
        tableName = tableName.substring(index + 1);
      }
      ResultSet rs = dbm.getColumns(currentCatalog, schema, tableName, null);
      DataTable dt = new DataTable(rs);

      rs = dbm.getPrimaryKeys(currentCatalog, null, tableName);
      DataTable keyDt = new DataTable(rs);
      Mapx map = keyDt.toMapx("Column_Name", "PK_Name");
      dt.insertColumn("isKey");
      for (int i = 0; i < dt.getRowCount(); i++) {
        DataRow dr = dt.getDataRow(i);
        if (map.containsKey(dr.getString("Column_Name")))
          dr.set("isKey", "Y");
        else {
          dr.set("isKey", "N");
        }
      }
      DataAccess da = new DataAccess(conn);
      DataTable data = da.executeDataTable(new QueryBuilder("select * from " + oldName + " where 1=2", new Object[0]));
      for (int i = 0; i < data.getColumnCount(); i++) {
        DataRow dr = dt.getDataRow(i);
        dr.set("Type_Name", Integer.valueOf(data.getDataColumn(i).getColumnType()));
      }
      return dt;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public static DataTable getSQLTypes() {
    return getSQLTypes(DBConnPool.getDBConnConfig());
  }

  public static DataTable getSQLTypes(DBConnConfig dcc) {
    Connection conn = null;
    try {
      conn = DBConnPoolImpl.createConnection(dcc, false);
      DatabaseMetaData dbm = conn.getMetaData();
      ResultSet rs = dbm.getTypeInfo();
      DataTable dt = new DataTable(rs);
      return dt;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public static int getCount(QueryBuilder qb)
  {
    return getCount(qb, null);
  }

  public static int getCount(QueryBuilder qb, String poolName) {
    DataAccess da = new DataAccess(DBConnPool.getConnection(poolName));
    try {
      return da.getCount(qb);
    } finally {
      da.close();
    }
  }

  public static Timestamp getOracleTimestamp(Object value) {
    try {
      Class clz = value.getClass();
      Method m = clz.getMethod("timestampValue", null);
      return (Timestamp)m.invoke(value, null); } catch (Exception e) {
    }
    return null;
  }

  public static String sqlBitAndFunction(String columnName, String n)
  {
    return sqlBitAndFunction(columnName, n, null);
  }

  public static String sqlBitAndFunction(String columnName, int n) {
    return sqlBitAndFunction(columnName, n+"", null);
  }

  public static String sqlBitAndFunction(String columnName, String n, String poolName)
  {
    DBConnConfig dcc = DBConnPool.getDBConnConfig(poolName);
    if ((dcc.isSQLServer()) || (dcc.isMysql()))
      return columnName + "&" + n;
    if ((dcc.isOracle()) || (dcc.isDB2())) {
      return "bitand(" + columnName + "," + n + ")";
    }
    return columnName + "&" + n;
  }
}