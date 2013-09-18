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
import com.zving.framework.utility.StringFormat;
import java.util.ArrayList;
import java.util.List;

public class DB2 extends AbstractDBType
{
  public static final String ID = "DB2";

  public String getID()
  {
    return "DB2";
  }

  public String getName() {
    return "DB2";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:db2://");
    sb.append(dcc.DBServerAddress);
    sb.append(":");
    sb.append(dcc.DBPort);
    sb.append("/");
    sb.append(dcc.DBName);
    return sb.toString();
  }

  public void afterConnectionCreate(DBConn conn) {
  }

  public int getDefaultPort() {
    return 50000;
  }

  public String getPKNameFragment(String table) {
    String pkName = table;
    if (pkName.length() > 15) {
      pkName = pkName.substring(0, 15);
    }
    return "constraint PK_" + pkName + " primary key";
  }

  public String toSQLType(int columnType, int length, int precision) {
    String type = null;
    if (columnType == 3)
      type = "DOUBLE PRECISION";
    else if (columnType == 2)
      type = "BLOB";
    else if (columnType == 12)
      type = "TIMESTAMP";
    else if (columnType == 4)
      type = "DECIMAL";
    else if (columnType == 6)
      type = "NUMERIC";
    else if (columnType == 5)
      type = "NUMERIC";
    else if (columnType == 8)
      type = "INTEGER";
    else if (columnType == 7)
      type = "BIGINT";
    else if (columnType == 9)
      type = "INTEGER";
    else if (columnType == 1)
      type = "VARCHAR";
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

  public String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    int start = pageIndex * pageSize;
    int end = (pageIndex + 1) * pageSize;
    StringBuilder sb = new StringBuilder();
    sb.append("select * from (select rs.*,rownumber() over () rnm from (");
    sb.append(qb.getSQL());
    sb.append(") rs) rss where rnm between ? and ?");
    qb.add(new Object[] { Integer.valueOf(start + 1) });
    qb.add(new Object[] { Integer.valueOf(end) });
    return sb.toString();
  }

  public String getDriverClass() {
    return "com.ibm.db2.jcc.DB2Driver";
  }

  public String[] toSQLArray(CreateTableCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(AddColumnCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(AlterKeyCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(AdvanceChangeColumnCommand c) {
    String[] arr = c.getDefaultSQLArray("DB2");
    List list = new ArrayList();
    for (String sql : arr) {
      list.add(sql);
      if (sql.startsWith("alter table")) {
        list.add("reorg table " + c.Table);
      }
    }
    arr = new String[list.size()];
    return (String[])list.toArray(arr);
  }

  public String[] toSQLArray(RenameColumnCommand c) {
    AdvanceChangeColumnCommand acc = new AdvanceChangeColumnCommand();
    acc.Table = c.Table;
    acc.Column = c.NewColumn;
    acc.DataType = c.DataType;
    acc.OldDataType = c.DataType;
    acc.Length = c.Length;
    acc.OldLength = c.Length;
    acc.Precision = c.Precision;
    acc.OldPrecision = c.Precision;
    acc.Mandatory = c.Mandatory;
    acc.OldMandatory = c.Mandatory;
    return toSQLArray(acc);
  }

  public String[] toSQLArray(ChangeColumnLengthCommand c) {
    String sql = StringFormat.format("alter table ? alter ? set data type ?", new Object[] { c.Table, c.Column, 
      toSQLType(c.DataType, c.Length, c.Precision) });
    String reorg = "reorg table " + c.Table;
    return new String[] { sql, reorg };
  }

  public String[] toSQLArray(CreateIndexCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(DropColumnCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(DropIndexCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(DropTableCommand c) {
    return c.getDefaultSQLArray("DB2");
  }

  public String[] toSQLArray(RenameTableCommand c) {
    return new String[] { "rename table " + c.Table + " to " + c.NewTable };
  }

  public String getSQLSperator() {
    return ";\n";
  }

  public String[] toSQLArray(ChangeColumnMandatoryCommand c) {
    String sql = null;
    if (c.Mandatory)
      sql = StringFormat.format("alter table ? alter ? set not null", new Object[] { c.Table, c.Column });
    else {
      sql = StringFormat.format("alter table ? alter ? drop not null", new Object[] { c.Table, c.Column });
    }
    String reorg = "reorg table " + c.Table;
    return new String[] { sql, reorg };
  }
}