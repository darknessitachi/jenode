package com.zving.framework.data;

import com.zving.framework.Config;
import com.zving.framework.config.LogSQL;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.data.exception.AlterException;
import com.zving.framework.data.exception.CloseException;
import com.zving.framework.data.exception.CommitException;
import com.zving.framework.data.exception.CreateException;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.data.exception.DeleteException;
import com.zving.framework.data.exception.DropException;
import com.zving.framework.data.exception.InsertException;
import com.zving.framework.data.exception.RollbackException;
import com.zving.framework.data.exception.SetAutoCommitException;
import com.zving.framework.data.exception.SetParamException;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringFormat;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataAccess
{
  protected DBConn conn;

  public DataAccess()
  {
  }

  public DataAccess(DBConn conn)
  {
    this.conn = conn;
  }

  public DBConn getConnection() {
    if (this.conn == null) {
      this.conn = DBConnPool.getConnection();
    }
    return this.conn;
  }

  public DataAccess setAutoCommit(boolean bCommit) {
    if (this.conn == null)
      this.conn = DBConnPool.getConnection();
    try
    {
      this.conn.setAutoCommit(bCommit);
    } catch (SQLException e) {
      throw new SetAutoCommitException(e);
    }
    return this;
  }

  public void commit() {
    if (this.conn == null)
      return;
    try
    {
      this.conn.commit();
    } catch (SQLException e) {
      throw new CommitException(e);
    }
  }

  public void rollback() {
    if (this.conn == null)
      return;
    try
    {
      this.conn.rollback();
    } catch (SQLException e) {
      throw new RollbackException(e);
    }
  }

  public void close() {
    if ((this.conn == null) || (this.conn == BlockingTransaction.getCurrentThreadConnection()))
      return;
    try
    {
      this.conn.close();
    } catch (SQLException e) {
      throw new CloseException(e);
    }
  }

  public static void setParams(PreparedStatement stmt, QueryBuilder qb, DBConn conn)
  {
    IDBType db = (IDBType)DBTypeService.getInstance().get(conn.getDBConfig().DBType);
    try {
      ArrayList batches = null;
      if (qb.isBatchMode()) {
        batches = qb.getBatches();
        for (int k = 0; k < batches.size(); k++) {
          ArrayList list = (ArrayList)batches.get(k);
          for (int i = 1; i <= list.size(); i++) {
            Object o = list.get(i - 1);
            if (o == null) {
              stmt.setNull(i, 12);
            } else if ((o instanceof Byte)) {
              stmt.setByte(i, ((Byte)o).byteValue());
            } else if ((o instanceof Short)) {
              stmt.setShort(i, ((Short)o).shortValue());
            } else if ((o instanceof Integer)) {
              stmt.setInt(i, ((Integer)o).intValue());
            } else if ((o instanceof Long)) {
              stmt.setLong(i, ((Long)o).longValue());
            } else if ((o instanceof Float)) {
              stmt.setFloat(i, ((Float)o).floatValue());
            } else if ((o instanceof Double)) {
              stmt.setDouble(i, ((Double)o).doubleValue());
            } else if ((o instanceof Date)) {
              stmt.setTimestamp(i, new Timestamp(((Date)o).getTime()));
            } else if ((o instanceof String)) {
              String str = (String)o;
              if ((conn.getDBConfig().isLatin1Charset) && (conn.getDBConfig().isOracle())) {
                try {
                  str = new String(str.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
                }
              }
              stmt.setString(i, str);
            } else if ((o instanceof Clob)) {
              db.setClob(conn, stmt, i, o);
            } else if ((o instanceof byte[])) {
              db.setBlob(conn, stmt, i, (byte[])o);
            } else {
              stmt.setObject(i, o);
            }
          }
          stmt.addBatch();
        }
      } else {
        ArrayList list = qb.getParams();
        for (int i = 1; i <= list.size(); i++) {
          Object o = list.get(i - 1);
          if (o == null) {
            stmt.setNull(i, 12);
          } else if ((o instanceof Byte)) {
            stmt.setByte(i, ((Byte)o).byteValue());
          } else if ((o instanceof Short)) {
            stmt.setShort(i, ((Short)o).shortValue());
          } else if ((o instanceof Integer)) {
            stmt.setInt(i, ((Integer)o).intValue());
          } else if ((o instanceof Long)) {
            stmt.setLong(i, ((Long)o).longValue());
          } else if ((o instanceof Float)) {
            stmt.setFloat(i, ((Float)o).floatValue());
          } else if ((o instanceof Double)) {
            stmt.setDouble(i, ((Double)o).doubleValue());
          } else if ((o instanceof Date)) {
            stmt.setTimestamp(i, new Timestamp(((Date)o).getTime()));
          } else if ((o instanceof String)) {
            String str = (String)o;
            if ((conn.getDBConfig().isLatin1Charset) && (conn.getDBConfig().isOracle())) {
              try {
                str = new String(str.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
              } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
              }

            }
            else
            {
              stmt.setString(i, str);
            }
          } else if ((o instanceof Clob)) {
            db.setClob(conn, stmt, i, o);
          } else if ((o instanceof byte[])) {
            db.setBlob(conn, stmt, i, (byte[])o);
          } else {
            stmt.setObject(i, o);
          }
        }
      }
    } catch (SQLException e) {
      throw new SetParamException(e);
    }
  }

  public DataTable fetch(QueryBuilder qb) {
    return executeDataTable(qb);
  }

  public DataTable executeDataTable(QueryBuilder qb) {
    if (this.conn == null) {
      this.conn = DBConnPool.getConnection();
    }
    long start = System.currentTimeMillis();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    DataTable dt = null;
    String sql = qb.getSQL();
    try {
      boolean latin1Flag = (this.conn.getDBConfig().isLatin1Charset) && (this.conn.getDBConfig().isOracle());
      if (latin1Flag) {
        try {
          sql = new String(sql.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      stmt = this.conn.prepareStatement(sql, 1003, 1007);
      setParams(stmt, qb, this.conn);
      rs = stmt.executeQuery();
      dt = new DataTable(rs, latin1Flag);
      this.conn.setLastSuccessExecuteTime(System.currentTimeMillis());
    } catch (SQLException e) {
      throw new QueryException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        log(start, sql, qb.getParams());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return dt;
  }

  public DataTable fetch(QueryBuilder qb, int pageSize, int pageIndex) {
    return executePagedDataTable(qb, pageSize, pageIndex);
  }

  public DataTable executePagedDataTable(QueryBuilder qb, int pageSize, int pageIndex)
  {
    long start = System.currentTimeMillis();
    if (this.conn == null) {
      this.conn = DBConnPool.getConnection();
    }
    if (pageSize < 1) {
      pageSize = 50;
    }
    if (pageIndex < 0) {
      pageIndex = 0;
    }
    String pagedSQL = getPagedSQL(this.conn, qb, pageSize, pageIndex);
    PreparedStatement stmt = null;
    ResultSet rs = null;
    DataTable dt = null;
    try {
      boolean latin1Flag = (this.conn.getDBConfig().isLatin1Charset) && (this.conn.getDBConfig().isOracle());
      if (latin1Flag) {
        try {
          pagedSQL = new String(pagedSQL.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      stmt = this.conn.prepareStatement(pagedSQL, 1003, 1007);
      setParams(stmt, qb, this.conn);
      rs = stmt.executeQuery();
      if (this.conn.getDBConfig().isSQLServer2000()) {
        dt = new DataTable(rs, pageSize, pageIndex, latin1Flag);
      } else {
        dt = new DataTable(rs, latin1Flag);
        if ((this.conn.getDBConfig().isOracle()) || (this.conn.getDBConfig().isDB2()) || (this.conn.getDBConfig().isSQLServer()) || 
          (this.conn.getDBConfig().isSybase())) {
          dt.deleteColumn(dt.getColumnCount() - 1);
        }
      }
      this.conn.setLastSuccessExecuteTime(System.currentTimeMillis());
    } catch (SQLException e) {
      LogUtil.warn(pagedSQL);
      throw new QueryException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        log(start, pagedSQL, qb.getParams());

        if ((!this.conn.getDBConfig().isSQLServer2000()) && (!this.conn.getDBConfig().isSybase())) {
          qb.getParams().remove(qb.getParams().size() - 1);
          qb.getParams().remove(qb.getParams().size() - 1);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return dt;
  }

  public static String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    IDBType db = (IDBType)DBTypeService.getInstance().get(conn.getDBConfig().DBType);
    return db.getPagedSQL(conn, qb, pageSize, pageIndex);
  }

  public int getCount(QueryBuilder qb)
  {
    QueryBuilder cqb = new QueryBuilder();
    cqb.setParams((ArrayList)qb.getParams().clone());
    String sql = qb.getSQL();
    int index1 = sql.lastIndexOf(")");
    int index2 = sql.toLowerCase().lastIndexOf("order by");
    if (index2 > index1) {
      sql = sql.substring(0, index2);
    }

    cqb.setSQL("select count(*) from (" + sql + ") t1");
    Object obj = executeOneValue(cqb);
    if (obj == null) {
      return 0;
    }
    return Integer.parseInt(obj.toString());
  }

  public Object executeOneValue(QueryBuilder qb) {
    long start = System.currentTimeMillis();
    if (this.conn == null) {
      this.conn = DBConnPool.getConnection();
    }
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Object t = null;
    String sql = qb.getSQL();
    try {
      boolean latin1Flag = (this.conn.getDBConfig().isLatin1Charset) && (this.conn.getDBConfig().isOracle());
      if (latin1Flag) {
        try {
          sql = new String(sql.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      stmt = this.conn.prepareStatement(sql, 1003, 1007);
      setParams(stmt, qb, this.conn);
      rs = stmt.executeQuery();
      if (rs.next()) {
        t = rs.getObject(1);
        if ((t instanceof Clob)) {
          t = DBUtil.clobToString((Clob)t);
        }
        if ((t instanceof Blob)) {
          t = DBUtil.blobToBytes((Blob)t);
        }
      }
      this.conn.setLastSuccessExecuteTime(System.currentTimeMillis());
    } catch (SQLException e) {
      throw new QueryException(e);
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        log(start, sql, qb.getParams());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return t;
  }

  public int executeNoQuery(QueryBuilder qb) {
    long start = System.currentTimeMillis();
    if (this.conn == null) {
      this.conn = DBConnPool.getConnection();
    }
    PreparedStatement stmt = null;
    int t = -1;
    String sql = qb.getSQL();
    try {
      if ((this.conn.getDBConfig().isLatin1Charset) && (this.conn.getDBConfig().isOracle())) {
        try {
          sql = new String(sql.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      stmt = this.conn.prepareStatement(sql, 1003, 1007);
      setParams(stmt, qb, this.conn);
      if (qb.isBatchMode())
        stmt.executeBatch();
      else {
        t = stmt.executeUpdate();
      }
      this.conn.setLastSuccessExecuteTime(System.currentTimeMillis());
    } catch (SQLException e) {
      String lowerSQL = sql.trim().toLowerCase();
      if (lowerSQL.startsWith("delete "))
        throw new DeleteException(e);
      if (lowerSQL.startsWith("update "))
        throw new DeleteException(e);
      if (lowerSQL.startsWith("insert "))
        throw new InsertException(e);
      if (lowerSQL.startsWith("create "))
        throw new CreateException(e);
      if (lowerSQL.startsWith("drop "))
        throw new DropException(e);
      if (lowerSQL.startsWith("alter ")) {
        throw new AlterException(e);
      }
      throw new DatabaseException(e);
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        log(start, sql, qb.getParams());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return t;
  }

  public static void log(long startTime, String sql, List<Object> params)
  {
    String message = sql;
    long time = System.currentTimeMillis() - startTime;
    if ((params != null) && (!sql.startsWith("Error:"))) {
      message = StringFormat.format(sql, params.toArray());
    }
    if (LogSQL.getValue()) {
      LogUtil.debug(time + "ms\t" + message);
    }

    ExtendManager.invoke("com.zving.framework.AfterSQLExecutedAction", new Object[] { Long.valueOf(time), message });
  }

  public DataAccess insert(DAO<?> dao) {
    dao.setDataAccess(this);
    dao.insert();
    return this;
  }

  public DataAccess update(DAO<?> dao) {
    dao.setDataAccess(this);
    dao.update();
    return this;
  }

  public DataAccess delete(DAO<?> dao) {
    dao.setDataAccess(this);
    dao.delete();
    return this;
  }

  public DataAccess deleteAndBackup(DAO<?> dao) {
    dao.setDataAccess(this);
    dao.deleteAndBackup();
    return this;
  }

  public DataAccess deleteAndInsert(DAO<?> dao) {
    dao.setDataAccess(this);
    dao.deleteAndInsert();
    return this;
  }

  public DataAccess insert(DAOSet<?> set) {
    set.setDataAccess(this);
    set.insert();
    return this;
  }

  public DataAccess update(DAOSet<?> set) {
    set.setDataAccess(this);
    set.update();
    return this;
  }

  public DataAccess delete(DAOSet<?> set) {
    set.setDataAccess(this);
    set.delete();
    return this;
  }

  public DataAccess deleteAndBackup(DAOSet<?> set) {
    set.setDataAccess(this);
    set.deleteAndBackup();
    return this;
  }

  public DataAccess deleteAndInsert(DAOSet<?> set) {
    set.setDataAccess(this);
    set.deleteAndInsert();
    return this;
  }
}