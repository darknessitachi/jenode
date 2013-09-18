package com.zving.framework.data.dbtype;

import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
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
import com.zving.framework.data.command.IDBCommand;
import com.zving.framework.data.command.RenameColumnCommand;
import com.zving.framework.data.command.RenameTableCommand;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDBType
  implements IDBType
{
  public Connection createConnection(DBConnConfig dcc)
    throws SQLException, ClassNotFoundException
  {
    Class.forName(getDriverClass());
    return DriverManager.getConnection(getJdbcUrl(dcc), dcc.DBUserName, dcc.DBPassword);
  }

  public void setBlob(DBConn conn, PreparedStatement ps, int i, byte[] v) throws SQLException {
    ps.setObject(i, v);
  }

  public void setClob(DBConn conn, PreparedStatement ps, int i, Object v) throws SQLException {
    ps.setObject(i, v);
  }

  public static String getFieldExtDesc(int length, int precision) {
    if (length != 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      sb.append(length);
      if (precision != 0) {
        sb.append(",");
        sb.append(precision);
      }
      sb.append(") ");
      return sb.toString();
    }
    return "";
  }

  public static String[] toSQLArray(IDBType db, IDBCommand c) {
    if ((c instanceof CreateTableCommand))
      return db.toSQLArray((CreateTableCommand)c);
    if ((c instanceof AddColumnCommand))
      return db.toSQLArray((AddColumnCommand)c);
    if ((c instanceof AlterKeyCommand))
      return db.toSQLArray((AlterKeyCommand)c);
    if ((c instanceof AdvanceChangeColumnCommand))
      return db.toSQLArray((AdvanceChangeColumnCommand)c);
    if ((c instanceof CreateIndexCommand))
      return db.toSQLArray((CreateIndexCommand)c);
    if ((c instanceof DropColumnCommand))
      return db.toSQLArray((DropColumnCommand)c);
    if ((c instanceof DropIndexCommand))
      return db.toSQLArray((DropIndexCommand)c);
    if ((c instanceof DropTableCommand))
      return db.toSQLArray((DropTableCommand)c);
    if ((c instanceof RenameTableCommand))
      return db.toSQLArray((RenameTableCommand)c);
    if ((c instanceof RenameColumnCommand))
      return db.toSQLArray((RenameColumnCommand)c);
    if ((c instanceof ChangeColumnLengthCommand))
      return db.toSQLArray((ChangeColumnLengthCommand)c);
    if ((c instanceof ChangeColumnMandatoryCommand)) {
      return db.toSQLArray((ChangeColumnMandatoryCommand)c);
    }
    return null;
  }

  public String getComment(String message) {
    String[] arr = message.split("\\n");
    StringBuilder sb = new StringBuilder();
    for (String line : arr) {
      if (sb.length() > 0) {
        sb.append("\n");
      }
      sb.append("--");
      sb.append(line.trim());
    }
    return sb.toString();
  }
}