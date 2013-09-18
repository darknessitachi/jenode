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
import com.zving.framework.extend.IExtendItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract interface IDBType extends IExtendItem
{
  public abstract boolean isFullSupport();

  public abstract String getDriverClass();

  public abstract Connection createConnection(DBConnConfig paramDBConnConfig)
    throws SQLException, ClassNotFoundException;

  public abstract String getJdbcUrl(DBConnConfig paramDBConnConfig);

  public abstract void afterConnectionCreate(DBConn paramDBConn)
    throws SQLException;

  public abstract int getDefaultPort();

  public abstract String[] toSQLArray(CreateTableCommand paramCreateTableCommand);

  public abstract String[] toSQLArray(AddColumnCommand paramAddColumnCommand);

  public abstract String[] toSQLArray(AlterKeyCommand paramAlterKeyCommand);

  public abstract String[] toSQLArray(AdvanceChangeColumnCommand paramAdvanceChangeColumnCommand);

  public abstract String[] toSQLArray(CreateIndexCommand paramCreateIndexCommand);

  public abstract String[] toSQLArray(DropColumnCommand paramDropColumnCommand);

  public abstract String[] toSQLArray(DropIndexCommand paramDropIndexCommand);

  public abstract String[] toSQLArray(DropTableCommand paramDropTableCommand);

  public abstract String[] toSQLArray(RenameTableCommand paramRenameTableCommand);

  public abstract String[] toSQLArray(RenameColumnCommand paramRenameColumnCommand);

  public abstract String[] toSQLArray(ChangeColumnLengthCommand paramChangeColumnLengthCommand);

  public abstract String[] toSQLArray(ChangeColumnMandatoryCommand paramChangeColumnMandatoryCommand);

  public abstract String getPKNameFragment(String paramString);

  public abstract String toSQLType(int paramInt1, int paramInt2, int paramInt3);

  public abstract void setBlob(DBConn paramDBConn, PreparedStatement paramPreparedStatement, int paramInt, byte[] paramArrayOfByte)
    throws SQLException;

  public abstract void setClob(DBConn paramDBConn, PreparedStatement paramPreparedStatement, int paramInt, Object paramObject)
    throws SQLException;

  public abstract String getPagedSQL(DBConn paramDBConn, QueryBuilder paramQueryBuilder, int paramInt1, int paramInt2);

  public abstract String getSQLSperator();

  public abstract String getComment(String paramString);
}