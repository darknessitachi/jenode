package com.zving.framework.data.dbtype;

import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.data.command.AddColumnCommand;
import com.zving.framework.data.command.AdvanceChangeColumnCommand;
import com.zving.framework.data.command.AlterKeyCommand;
import com.zving.framework.data.command.ChangeColumnLengthCommand;
import com.zving.framework.data.command.ChangeColumnMandatoryCommand;
import com.zving.framework.data.command.CreateIndexCommand;
import com.zving.framework.data.command.CreateTableCommand;
import com.zving.framework.data.command.DropColumnCommand;
import com.zving.framework.data.command.DropIndexCommand;
import com.zving.framework.data.command.DropTableCommand;
import com.zving.framework.data.command.RenameColumnCommand;
import com.zving.framework.data.command.RenameTableCommand;
import com.zving.framework.utility.ObjectUtil;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Oracle extends AbstractDBType
{
  public static final String ID = "ORACLE";

  public String getID()
  {
    return "ORACLE";
  }

  public String getName() {
    return "Oracle";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:oracle:thin:@");
    sb.append(dcc.DBServerAddress);
    sb.append(":");
    sb.append(dcc.DBPort);
    sb.append(":");
    sb.append(dcc.DBName);
    return sb.toString();
  }

  public Connection createConnection(DBConnConfig dcc) throws SQLException, ClassNotFoundException {
    Class.forName("oracle.jdbc.driver.OracleDriver");
    Properties props = new Properties();
    props.setProperty("user", dcc.DBUserName);
    props.setProperty("password", dcc.DBPassword);
    props.setProperty("oracle.jdbc.V8Compatible", "true");
    return DriverManager.getConnection(getJdbcUrl(dcc), props);
  }

  public void afterConnectionCreate(DBConn conn) throws SQLException
  {
    Statement stmt = conn.createStatement(1005, 1008);
    stmt.execute("alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS'");
    stmt.close();
  }

  public int getDefaultPort() {
    return 1521;
  }

  public String getTableDropSQL(String tableCode) {
    return "drop table " + tableCode + " cascade constraints";
  }

  public String toSQLType(int columnType, int length, int precision) {
    String type = null;
    if (columnType == 3)
      type = "DOUBLE PRECISION";
    else if (columnType == 2)
      type = "BLOB";
    else if (columnType == 12)
      type = "DATE";
    else if (columnType == 4)
      type = "DECIMAL";
    else if (columnType == 6)
      type = "NUMBER";
    else if (columnType == 5)
      type = "NUMBER";
    else if (columnType == 8)
      type = "INTEGER";
    else if (columnType == 7)
      type = "INTEGER";
    else if (columnType == 9)
      type = "INTEGER";
    else if (columnType == 1)
      type = "VARCHAR2";
    else if (columnType == 10) {
      type = "CLOB";
    }
    if (ObjectUtil.empty(type)) {
      throw new RuntimeException("Unknown DBType " + getID() + " or DataType" + columnType);
    }
    if ((length == 0) && (columnType == 1)) {
      throw new RuntimeException("varchar's length can't be empty!");
    }
    return type + getFieldExtDesc(length, precision);
  }

  public void setBlob(DBConn conn, PreparedStatement ps, int i, byte[] v) throws SQLException {
    Class blobClass = null;
    try {
      blobClass = Class.forName("oracle.sql.BLOB");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      return;
    }
    Object blob = null;
    Object oc = conn.getPhysicalConnection();
    try {
      Method m = blobClass.getMethod("createTemporary", new Class[] { Connection.class, Boolean.TYPE, Integer.TYPE });
      blob = m.invoke(null, new Object[] { oc, new Boolean(true), new Integer(1) });

      m = blobClass.getMethod("open", new Class[] { Integer.TYPE });
      m.invoke(blob, new Object[] { new Integer(1) });

      m = blobClass.getMethod("getBinaryOutputStream", new Class[] { Long.TYPE });
      OutputStream out = (OutputStream)m.invoke(blob, new Object[] { new Long(0L) });
      out.write(v);
      out.close();

      blobClass.getMethod("close", null).invoke(blob, null);
      ps.setBlob(i, (Blob)blob);
    } catch (Exception e) {
      try {
        if (blob != null) {
          Method m = blobClass.getMethod("freeTemporary", new Class[] { blobClass });
          m.invoke(null, new Object[] { blob });
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  public void setClob(DBConn conn, PreparedStatement ps, int i, Object v) throws SQLException {
    Class clobClass = null;
    try {
      clobClass = Class.forName("oracle.sql.CLOB");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      return;
    }
    Object clob = null;
    Object oc = conn.getPhysicalConnection();
    try {
      Method m = clobClass.getMethod("createTemporary", new Class[] { Connection.class, Boolean.TYPE, Integer.TYPE });
      clob = m.invoke(null, new Object[] { oc, new Boolean(true), new Integer(1) });

      m = clobClass.getMethod("open", new Class[] { Integer.TYPE });
      m.invoke(clob, new Object[] { new Integer(1) });

      m = clobClass.getMethod("setCharacterStream", new Class[] { Long.TYPE });
      Writer writer = (Writer)m.invoke(clob, new Object[] { new Long(0L) });

      writer.write(ObjectUtil.toString(v));
      writer.close();

      clobClass.getMethod("close", null).invoke(clob, null);
      ps.setClob(i, (Clob)clob);
    } catch (Exception e) {
      try {
        if (clob != null) {
          Method m = clobClass.getMethod("freeTemporary", new Class[] { clobClass });
          m.invoke(null, new Object[] { clob });
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  public String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    StringBuilder sb = new StringBuilder();
    int start = pageIndex * pageSize;
    int end = (pageIndex + 1) * pageSize;
    sb.append("select * from (select rs.*,rownum rnm from (");
    sb.append(qb.getSQL());
    sb.append(") rs where rownum <= ?) rss where rnm > ?");
    qb.add(new Object[] { Integer.valueOf(end) });
    qb.add(new Object[] { Integer.valueOf(start) });
    return sb.toString();
  }

  public String getDriverClass() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  public String[] toSQLArray(CreateTableCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(AddColumnCommand c) {
    String sql = "alter table " + c.Table + " add " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public String[] toSQLArray(AlterKeyCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(AdvanceChangeColumnCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(CreateIndexCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(DropColumnCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(DropIndexCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(DropTableCommand c) {
    String sql = "drop table " + c.Table + " cascade constraints";
    return new String[] { sql };
  }

  public String[] toSQLArray(RenameTableCommand c) {
    return new String[] { "rename " + c.Table + " to " + c.NewTable };
  }

  public String getPKNameFragment(String table) {
    return "constraint PK_" + table + " primary key";
  }

  public String getSQLSperator() {
    return ";\n";
  }

  public String[] toSQLArray(RenameColumnCommand c) {
    return c.getDefaultSQLArray("ORACLE");
  }

  public String[] toSQLArray(ChangeColumnLengthCommand c) {
    String sql = "alter table " + c.Table + " modify " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
    String sql = "alter table " + c.Table + " modify " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }
}