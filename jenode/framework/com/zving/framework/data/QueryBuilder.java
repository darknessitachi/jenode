package com.zving.framework.data;

import com.zving.framework.data.exception.DatabaseException;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryBuilder
{
  private ArrayList<Object> list = new ArrayList();
  private ArrayList<ArrayList<Object>> batches;
  private StringBuilder sql = new StringBuilder();
  private boolean batchMode;

  protected ArrayList<ArrayList<Object>> getBatches()
  {
    return this.batches;
  }

  public QueryBuilder()
  {
  }

  public QueryBuilder(String sql, Object[] args)
  {
    setSQL(sql);
    if (args != null)
      for (Object obj : args)
        add(new Object[] { obj });
  }

  public boolean isBatchMode()
  {
    return this.batchMode;
  }

  public void setBatchMode(boolean batchMode)
  {
    if ((batchMode) && (this.batches == null)) {
      this.batches = new ArrayList();
    }
    this.batchMode = batchMode;
  }

  public void addBatch()
  {
    if (!this.batchMode) {
      throw new RuntimeException("Must invoke setBatchMode(true) before addBatch()");
    }
    this.batches.add(this.list);
    this.list = new ArrayList();
  }

  public QueryBuilder add(Object[] params)
  {
    for (Object param : params) {
      this.list.add(param);
    }
    return this;
  }

  public QueryBuilder set(int index, Object param)
  {
    this.list.set(index, param);
    return this;
  }

  public QueryBuilder setSQL(String sql)
  {
    this.sql = new StringBuilder(sql);
    return this;
  }

  public QueryBuilder append(String sqlPart, Object[] params)
  {
    this.sql.append(sqlPart);
    if (params == null) {
      return this;
    }
    for (Object obj : params) {
      add(new Object[] { obj });
    }
    return this;
  }

  public DataTable executeDataTable()
  {
    return executeDataTable(null);
  }

  public DataTable executeDataTable(String poolName)
  {
    DataAccess da = new DataAccess(DBConnPool.getConnection(poolName));
    DataTable dt = null;
    try {
      dt = da.executeDataTable(this);
    } catch (Throwable e) {
      DataAccess.log(System.currentTimeMillis(), "Error:" + e.getMessage(), null);
      throw new QueryException(e);
    } finally {
      da.close();
    }
    return dt;
  }

  public DataTable executePagedDataTable(int pageSize, int pageIndex)
  {
    return executePagedDataTable(null, pageSize, pageIndex);
  }

  public DataTable executePagedDataTable(String poolName, int pageSize, int pageIndex) {
    DataAccess da = new DataAccess(DBConnPool.getConnection(poolName));
    DataTable dt = null;
    try {
      dt = da.executePagedDataTable(this, pageSize, pageIndex);
    } catch (Throwable e) {
      DataAccess.log(System.currentTimeMillis(), "Error:" + e.getMessage(), null);
      throw new QueryException(e);
    } finally {
      da.close();
    }
    return dt;
  }

  public Object executeOneValue()
  {
    return executeOneValue(null);
  }

  public Object executeOneValue(String poolName) {
    DataAccess da = new DataAccess(DBConnPool.getConnection(poolName));
    Object t = null;
    try {
      t = da.executeOneValue(this);
    } catch (Throwable e) {
      throw new QueryException(e);
    } finally {
      da.close();
    }
    return t;
  }

  public String executeString()
  {
    return executeString(null);
  }

  public String executeString(String poolName) {
    Object o = executeOneValue(poolName);
    if (o == null) {
      return null;
    }
    return o.toString();
  }

  public int executeInt()
  {
    return executeInt(null);
  }

  public int executeInt(String poolName) {
    Object o = executeOneValue(poolName);
    if (o == null) {
      return 0;
    }
    return Integer.parseInt(o.toString());
  }

  public long executeLong()
  {
    return executeLong(null);
  }

  public long executeLong(String poolName) {
    Object o = executeOneValue(poolName);
    if (o == null) {
      return 0L;
    }
    return Long.parseLong(o.toString());
  }

  public int executeNoQueryWithException()
    throws SQLException
  {
    return executeNoQueryWithException(null);
  }

  public int executeNoQueryWithException(String poolName) throws SQLException {
    DataAccess da = new DataAccess(DBConnPool.getConnection(poolName));
    int t = -1;
    try {
      t = da.executeNoQuery(this);
    } catch (DatabaseException e) {
      if ((e.getCause() != null) && ((e.getCause() instanceof SQLException))) {
        throw ((SQLException)e.getCause());
      }
      throw new SQLException(e.getMessage());
    }
    finally {
      da.close();
    }
    return t;
  }

  public int executeNoQuery()
  {
    return executeNoQuery(null);
  }

  public int executeNoQuery(String poolName) {
    try {
      return executeNoQueryWithException(poolName);
    } catch (Throwable e) {
      throw new QueryException(e);
    }
  }

  public String getSQL()
  {
    return this.sql.toString();
  }

  public ArrayList<Object> getParams()
  {
    return this.list;
  }

  public void setParams(ArrayList<Object> list)
  {
    this.list = list;
  }

  public void clearBatches()
  {
    if (this.batchMode) {
      if (this.batches != null) {
        this.batches.clear();
      }
      this.batches = new ArrayList();
    }
  }

  public boolean checkParams()
  {
    char[] arr = this.sql.toString().toCharArray();
    boolean StringCharFlag = false;
    int count = 0;
    for (int i = 0; i < arr.length; i++) {
      char c = arr[i];
      if (c == '\'') {
        if (!StringCharFlag)
          StringCharFlag = true;
        else
          StringCharFlag = false;
      }
      else if ((c == '?') && 
        (!StringCharFlag)) {
        count++;
      }
    }

    if (count != this.list.size()) {
      throw new QueryException("SQL has " + count + " parameter，but value count is " + this.list.size());
    }
    return true;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.sql);
    sb.append("\t{");
    for (int i = 0; i < this.list.size(); i++) {
      if (i != 0) {
        sb.append(",");
      }
      Object o = this.list.get(i);
      if (o == null) {
        sb.append("null");
      }
      else {
        String str = this.list.get(i).toString();
        if (str.length() > 40) {
          str = str.substring(0, 37);
          sb.append(str);
          sb.append("...");
        } else {
          sb.append(str);
        }
      }
    }
    sb.append("}");
    return sb.toString();
  }

  public DataTable fetch() {
    return executeDataTable();
  }

  public DataTable fetch(int pageSize, int pageIndex) {
    return executePagedDataTable(pageSize, pageIndex);
  }

  public DataTable fetch(String poolName) {
    return executeDataTable(poolName);
  }

  public DataTable fetch(String poolName, int pageSize, int pageIndex) {
    return executePagedDataTable(poolName, pageSize, pageIndex);
  }

  public int execute() {
    return executeNoQuery();
  }

  public int execute(String poolName) {
    return executeNoQuery(poolName);
  }
}