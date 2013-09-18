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

public class HSQLDB extends AbstractDBType
{
  public static final String ID = "HSQLDB";

  public String getID()
  {
    return "HSQLDB";
  }

  public String getName() {
    return "HSQLDB";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder(128);
    if ("file".equalsIgnoreCase(dcc.DBServerAddress)) {
      sb.append("jdbc:hsqldb:res:");
      sb.append(dcc.DBName);
    } else if ("memory".equalsIgnoreCase(dcc.DBServerAddress)) {
      sb.append("jdbc:hsqldb:mem:.");
    } else {
      sb.append("jdbc:hsqldb:hsql://");
      sb.append(dcc.DBServerAddress);
      sb.append(":");
      sb.append(dcc.DBPort);
      sb.append("/");
      sb.append(dcc.DBName);
    }
    return sb.toString();
  }

  public void afterConnectionCreate(DBConn conn) {
  }

  public int getDefaultPort() {
    return 9001;
  }

  public String getPKNameFragment(String table) {
    return "primary key";
  }

  public String toSQLType(int columnType, int length, int precision) {
    String type = null;
    if (columnType == 3)
      type = "double";
    else if (columnType == 2)
      type = "binary varying(MAX)";
    else if (columnType == 12)
      type = "datetime";
    else if (columnType == 4)
      type = "decimal";
    else if (columnType == 6)
      type = "double";
    else if (columnType == 5)
      type = "float";
    else if (columnType == 8)
      type = "int";
    else if (columnType == 7)
      type = "bigint";
    else if (columnType == 9)
      type = "int";
    else if (columnType == 1)
      type = "varchar";
    else if (columnType == 10) {
      type = "longvarchar";
    }
    if (ObjectUtil.empty(type)) {
      throw new RuntimeException("Unknown DBType " + getID() + " or DataType" + columnType);
    }
    if ((length == 0) && (columnType == 1)) {
      throw new RuntimeException("varchar's length can't be empty!");
    }

    String FieldExtDesc = "";
    if (length != 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      sb.append(length);
      if (precision != 0) {
        sb.append(",");
        sb.append(precision);
      }
      sb.append(") ");
      FieldExtDesc = sb.toString();
    }

    return type + FieldExtDesc;
  }

  public String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    StringBuilder sb = new StringBuilder();
    int start = pageIndex * pageSize;
    sb.append("select limit ? ? * from(");
    sb.append(qb.getSQL());
    sb.append(")");
    qb.add(new Object[] { Integer.valueOf(start) });
    qb.add(new Object[] { Integer.valueOf(pageSize) });
    return sb.toString();
  }

  public String[] toSQLArray(CreateTableCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(AddColumnCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(AlterKeyCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(AdvanceChangeColumnCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(CreateIndexCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(DropColumnCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(DropIndexCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(DropTableCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(RenameTableCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String getDriverClass() {
    return "org.hsqldb.jdbcDriver";
  }

  public String getSQLSperator() {
    return ";\n";
  }

  public String[] toSQLArray(RenameColumnCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(ChangeColumnLengthCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }

  public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
    return c.getDefaultSQLArray("HSQLDB");
  }
}