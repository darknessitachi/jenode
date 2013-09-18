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
import com.zving.framework.data.sql.SelectSQLParser;
import com.zving.framework.utility.ObjectUtil;

public class SQLServer2005 extends AbstractDBType
{
  public static final String ID = "MSSQL";

  public String getID()
  {
    return "MSSQL";
  }

  public String getName() {
    return "SQLServer 2005";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:sqlserver://");
    sb.append(dcc.DBServerAddress);
    sb.append(":");
    sb.append(dcc.DBPort);
    sb.append(";DatabaseName=");
    sb.append(dcc.DBName);
    return sb.toString();
  }

  public void afterConnectionCreate(DBConn conn) {
  }

  public int getDefaultPort() {
    return 1433;
  }

  public String getPKNameFragment(String table) {
    return "constraint PK_" + table + " primary key nonclustered";
  }

  public String toSQLType(int columnType, int length, int precision) {
    String type = null;
    if (columnType == 3)
      type = "numeric";
    else if (columnType == 2)
      type = "varbinary(MAX)";
    else if (columnType == 12)
      type = "datetime";
    else if (columnType == 4)
      type = "numeric";
    else if (columnType == 6)
      type = "numeric";
    else if (columnType == 5)
      type = "numeric";
    else if (columnType == 8)
      type = "int";
    else if (columnType == 7)
      type = "bigint";
    else if (columnType == 9)
      type = "smallint";
    else if (columnType == 1)
      type = "varchar";
    else if (columnType == 10) {
      type = "text";
    }
    if (ObjectUtil.empty(type)) {
      throw new RuntimeException("Unknown DBType " + getID() + " or DataType" + columnType);
    }
    if ((length == 0) && (columnType == 1)) {
      throw new RuntimeException("varchar's length can't be empty!");
    }
    return type + getFieldExtDesc(length, precision);
  }

  public String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    StringBuilder sb = new StringBuilder();
    int start = pageIndex * pageSize;
    int end = (pageIndex + 1) * pageSize;
    String sql = qb.getSQL();
    SelectSQLParser sp = new SelectSQLParser();
    try {
      sp.setSQL(sql);
      sp.parse();
    } catch (Exception e) {
      e.printStackTrace();
    }
    sb.append(sp.getMSSQLPagedSQL());
    qb.add(new Object[] { Integer.valueOf(start + 1) });
    qb.add(new Object[] { Integer.valueOf(end) });
    return sb.toString();
  }

  public String[] toSQLArray(CreateTableCommand c) {
    return c.getDefaultSQLArray("MSSQL");
  }

  public String[] toSQLArray(AddColumnCommand c) {
    String sql = "alter table " + c.Table + " add " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " default '' not null";
    }
    return new String[] { sql };
  }

  public String[] toSQLArray(AlterKeyCommand c) {
    return c.getDefaultSQLArray("MSSQL");
  }

  public String[] toSQLArray(AdvanceChangeColumnCommand c) {
    return c.getDefaultSQLArray("MSSQL");
  }

  public String[] toSQLArray(CreateIndexCommand c) {
    return c.getDefaultSQLArray("MSSQL");
  }

  public String[] toSQLArray(DropColumnCommand c) {
    return c.getDefaultSQLArray("MSSQL");
  }

  public String[] toSQLArray(DropIndexCommand c) {
    return new String[] { "drop index " + c.Table + "." + c.Name };
  }

  public String[] toSQLArray(DropTableCommand c) {
    String sql = "if exists (select 1 from  sysobjects where id = object_id('" + c.Table + "') and type='U') drop table " + c.Table;
    return new String[] { sql };
  }

  public String[] toSQLArray(RenameTableCommand c) {
    return new String[] { "EXEC sp_rename '" + c.Table + "', '" + c.NewTable + "'" };
  }

  public String getDriverClass() {
    return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  }

  public String getSQLSperator() {
    return "\ngo\n";
  }

  public String[] toSQLArray(RenameColumnCommand c) {
    return new String[] { "EXEC sp_rename '" + c.Table + "." + c.Column + "', " + c.NewColumn + ", 'column'" };
  }

  public String[] toSQLArray(ChangeColumnLengthCommand c) {
    String sql = "alter table " + c.Table + " alter column " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
    String sql = "alter table " + c.Table + " alter column " + c.Column + " " + toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " default '' not null";
    }
    return new String[] { sql };
  }
}