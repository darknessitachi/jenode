package com.zving.framework.data.dbtype;

import com.zving.framework.Config;
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
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends AbstractDBType
{
  public static final String ID = "MYSQL";

  public String getID()
  {
    return "MYSQL";
  }

  public String getName() {
    return "Mysql";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:mysql://");
    sb.append(dcc.DBServerAddress);
    sb.append(":");
    sb.append(dcc.DBPort);
    sb.append("/");
    sb.append(dcc.DBName);
    return sb.toString();
  }

  public void afterConnectionCreate(DBConn conn) {
    try {
      Statement stmt = conn.createStatement(1005, 1008);
      String charset = conn.getDBConfig().Charset;
      if (ObjectUtil.empty(charset)) {
        charset = Config.getGlobalCharset();
      }
      stmt.execute("SET NAMES '" + charset.replaceAll("\\-", "").toLowerCase() + "'");
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int getDefaultPort() {
    return 3306;
  }

  public String getPKNameFragment(String table) {
    return "primary key";
  }

  public String toSQLType(int columnType, int length, int precision) {
    String type = null;
    if (columnType == 3)
      type = "double";
    else if (columnType == 2)
      type = "longblob";
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
      type = "mediumtext";
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
    sb.append(qb.getSQL()).append(" limit ?,?");
    qb.add(new Object[] { Integer.valueOf(start) });
    qb.add(new Object[] { Integer.valueOf(pageSize) });
    return sb.toString();
  }

  public String[] toSQLArray(CreateTableCommand c) {
    String[] arr = c.getDefaultSQLArray("MYSQL");
    arr[0] = (arr[0] + " engine=innodb");
    return arr;
  }

  public String[] toSQLArray(AddColumnCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String[] toSQLArray(AlterKeyCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String[] toSQLArray(AdvanceChangeColumnCommand c) {
    String sql = "alter table " + c.Table + " modify column " + c.Column + " " + 
      toSQLType(c.DataType, c.Length, c.Precision);
    if (c.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public String[] toSQLArray(CreateIndexCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String[] toSQLArray(DropColumnCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String[] toSQLArray(DropTableCommand c) {
    String sql = "drop table if exists " + c.Table;
    return new String[] { sql };
  }

  public String[] toSQLArray(DropIndexCommand c) {
    return new String[] { "alter table " + c.Table + " drop index " + c.Name };
  }

  public String[] toSQLArray(RenameTableCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String getDriverClass() {
    return "com.mysql.jdbc.Driver";
  }

  public String getSQLSperator() {
    return ";\n";
  }

  public String[] toSQLArray(RenameColumnCommand c) {
    return new String[] { "alter table " + c.Table + " change " + c.Column + " " + c.NewColumn + " " + toSQLType(c.DataType, c.Length, c.Precision) };
  }

  public String[] toSQLArray(ChangeColumnLengthCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }

  public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
    return c.getDefaultSQLArray("MYSQL");
  }
}