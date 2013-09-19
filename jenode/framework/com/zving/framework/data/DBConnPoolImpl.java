package com.zving.framework.data;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConnPoolImpl
{
  private DBConnConfig dcc;
  protected DBConn[] conns;
  private static Mapx<String, String> jndiMap = new Mapx(1000);

  public DBConnPoolImpl(String poolName)
  {
    this.dcc = new DBConnConfig();
    this.dcc.PoolName = poolName;
  }

  public DBConnPoolImpl(DBConnConfig config) {
    this.dcc = config;
    if (DBConnPool.getPoolMap().get(this.dcc.PoolName + ".") != null) {
      throw new DatabaseException("DB Connection Pool is exist:" + this.dcc.PoolName);
    }
    DBConnPool.getPoolMap().put(this.dcc.PoolName + ".", this);
    fillInitConn();
  }

  private void fillInitConn() {
    if (!this.dcc.isJNDIPool) {
      this.conns = new DBConn[this.dcc.MaxConnCount];
      try {
        for (int i = 0; i < this.dcc.InitConnCount; i++) {
          this.conns[i] = createConnection(this.dcc, false);
          this.conns[i].isUsing = false;
        }
        this.dcc.ConnCount = this.dcc.InitConnCount;
        LogUtil.info("----" + this.dcc.PoolName + " init " + this.dcc.InitConnCount + " connection");
      } catch (Exception e) {
        LogUtil.warn("----" + this.dcc.PoolName + "init Connections failed");
        e.printStackTrace();
      }
    }
  }

  public DBConn[] getDBConns() {
    return this.conns;
  }

  public void clear() {
    if (this.conns == null) {
      return;
    }
    for (int i = 0; i < this.conns.length; i++) {
      if (this.conns[i] != null) {
        try {
          this.conns[i].Conn.close();
          this.conns[i] = null;
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    this.dcc.ConnCount = 0;
  }

  public String getDBType() {
    return this.dcc.DBType;
  }

  public DBConnConfig getDBConnConfig() {
    return this.dcc;
  }

  public synchronized void init() {
    if (this.dcc.DBType != null) {
      return;
    }
    init(this.dcc, Config.getMapx());
    fillInitConn();
  }

  public static void init(DBConnConfig dcc, Mapx<String, String> map) {
    if (dcc.DBType != null) {
      return;
    }
    dcc.DBType = map.getString("Database." + dcc.PoolName + "Type");
    dcc.JNDIName = map.getString("Database." + dcc.PoolName + "JNDIName");
    dcc.Properties = map;
    dcc.isLatin1Charset = "true".equalsIgnoreCase(map.getString("Database." + dcc.PoolName + "isLatin1Charset"));
    if (StringUtil.isNotEmpty(dcc.JNDIName)) {
      dcc.isJNDIPool = true;
    } else {
      dcc.DBServerAddress = map.getString("Database." + dcc.PoolName + "ServerAddress");
      dcc.ConnectionURL = map.getString("Database." + dcc.PoolName + "ConnectionURL");
      dcc.DBName = map.getString("Database." + dcc.PoolName + "Name");
      dcc.DBUserName = map.getString("Database." + dcc.PoolName + "UserName");
      dcc.DBPassword = map.getString("Database." + dcc.PoolName + "Password");
      dcc.TestTable = map.getString("Database." + dcc.PoolName + "TestTable");
      if ((dcc.DBType == null) || (dcc.DBType.equals(""))) {
        throw new DatabaseException("DB.Type not found");
      }

      if (ObjectUtil.empty(dcc.ConnectionURL)) {
        if ((dcc.DBServerAddress == null) || (dcc.DBServerAddress.equals(""))) {
          throw new DatabaseException("DB.ServerAddress not found");
        }
        if ((dcc.DBName == null) || (dcc.DBName.equals(""))) {
          throw new DatabaseException("DB.Name not found");
        }
      }

      if ((dcc.DBUserName == null) || (dcc.DBUserName.equals(""))) {
        throw new DatabaseException("DB.UserName not found");
      }
      if (dcc.DBPassword == null) {
        throw new DatabaseException("DB.Password not found");
      }
      String s = map.getString("Database." + dcc.PoolName + "InitConnCount");
      try {
        dcc.InitConnCount = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        dcc.InitConnCount = 0;
        LogUtil.warn(s + " is invalid DB.InitConnCount value,will use 0");
      }
      s = map.getString("Database." + dcc.PoolName + "MaxConnCount");
      try {
        dcc.MaxConnCount = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        dcc.MaxConnCount = 20;
        LogUtil.warn(s + " is invalid DB.MaxConnCount value,will use 20");
      }
      s = map.getString("Database." + dcc.PoolName + "Port");
      try {
        dcc.DBPort = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        IDBType t = (IDBType)DBTypeService.getInstance().get(dcc.DBType);

        dcc.DBPort = t.getDefaultPort();
        LogUtil.warn(s + " is invalid DB.Port value,will use default value");
      }
    }
    if (dcc.InitConnCount < 5)
      dcc.InitConnCount = 5;
  }

  public DBConn getConnection()
  {
    return getConnection(false);
  }

  public DBConn getConnection(boolean bLongTimeFlag) {
    if (this.dcc.DBType == null) {
      init();
    }
    if (this.dcc.isJNDIPool) {
      return getJNDIPoolConnection(this.dcc);
    }
    long now = System.currentTimeMillis();
    DBConn conn = null;
    synchronized (this) {
      for (int i = 0; i < this.dcc.ConnCount; i++) {
        conn = this.conns[i];
        if (conn != null)
        {
          if (conn.isUsing) {
            if (!conn.LongTimeFlag) {
              if (now - conn.LastSuccessExecuteTime <= this.dcc.MaxConnUsingTime) continue;
              LogUtil.error(this.dcc.PoolName + 
                ":connection timeout,will close connection automatical,there is last sql and invoke stack:");
              LogUtil.error("Last SQL:" + conn.LastSQL);
              LogUtil.warn(getCallerStack(conn));
              DataAccess.log(conn.LastSuccessExecuteTime, "Timeout:" + conn.LastSQL, null);
              final DBConn conn2 = conn;
              new Runnable() {
                public void run() {
                  try {
                    if (!conn2.Conn.getAutoCommit())
                      conn2.Conn.rollback();
                  }
                  catch (SQLException e) {
                    e.printStackTrace();
                    try
                    {
                      conn2.Conn.close();
                    } catch (SQLException ex) {
                      ex.printStackTrace();
                    }
                  }
                  finally
                  {
                    try
                    {
                      conn2.Conn.close();
                    } catch (SQLException e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
              .run();
              try {
                conn = createConnection(this.dcc, bLongTimeFlag);
                this.conns[i] = conn;
                LogUtil.info(this.dcc.PoolName + ":create a new connection,total is " + this.dcc.ConnCount);
                setCaller(conn);
                conn.LastSuccessExecuteTime = now;
                return conn;
              } catch (Exception e) {
                e.printStackTrace();
                throw new DatabaseException("DBConnPoolImpl," + this.dcc.PoolName + "create new connection failed:" + 
                  e.getMessage());
              }
            }
            if ((now - conn.LastSuccessExecuteTime > 4 * this.dcc.MaxConnUsingTime) && (now - conn.LastWarnTime > 300000L)) {
              LogUtil.warn(this.dcc.PoolName + ":connection used " + (now - conn.LastSuccessExecuteTime) + 
                " ms,there is invoke stack:");
              LogUtil.warn(getCallerStack(conn));
              conn.LastWarnTime = now;
            }
          } else if (!conn.isBlockingTransactionStarted) {
            conn.LongTimeFlag = bLongTimeFlag;
            conn.isUsing = true;
            conn.LastApplyTime = now;
            setCaller(conn);

            keepAlive(conn);
            return conn;
          }
        }
      }
      if (this.dcc.ConnCount < this.dcc.MaxConnCount) {
        try {
          conn = createConnection(this.dcc, bLongTimeFlag);
          this.conns[this.dcc.ConnCount] = conn;
          this.dcc.ConnCount += 1;
          LogUtil.info(this.dcc.PoolName + ":create a new connection,total is " + this.dcc.ConnCount);
          setCaller(conn);
          conn.LastSuccessExecuteTime = now;
          return conn;
        } catch (Exception e) {
          e.printStackTrace();
          throw new DatabaseException("DBConnPoolImpl," + this.dcc.PoolName + ":create new connection failed:" + e.getMessage());
        }
      }
      throw new DatabaseException("DBConnPoolImpl," + this.dcc.PoolName + ":all connection is using!");
    }
  }

  private void keepAlive(DBConn conn)
  {
    if (conn == null) {
      return;
    }
    if (System.currentTimeMillis() - conn.getLastSuccessExecuteTime() > this.dcc.RefershPeriod) {
      DataAccess dAccess = new DataAccess(conn);
      try {
        dAccess.executeOneValue(new QueryBuilder("select 1 from " + this.dcc.TestTable + " where 1=2", new Object[0]));
      } catch (Exception e) {
        try {
          conn.Conn.close();
        } catch (SQLException localSQLException) {
        }
        try {
          conn.Conn = createConnection(this.dcc, false).Conn;
        } catch (Exception e1) {
          LogUtil.error(this.dcc.PoolName + ":Reconnection is failed:" + e1.getMessage());
        }
      }
    }
  }

  public void keepAlive() {
    synchronized (this) {
      int count = 0;
      for (int i = 0; i < this.conns.length; i++) {
        DBConn conn = this.conns[i];
        if ((conn != null) && (!conn.isUsing))
        {
          if (count < this.dcc.InitConnCount) {
            count++;
            keepAlive(conn);
            conn.LastSuccessExecuteTime = System.currentTimeMillis();
          } else {
            this.conns[i] = null;
            this.dcc.ConnCount -= 1;
            try {
              conn.Conn.close();
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  private static DBConn getJNDIPoolConnection(DBConnConfig dbcc)
  {
    int connID = DBConn.getConnID();
    try {
      Context ctx = new InitialContext();
      Connection conn = null;
      if (Config.isTomcat()) {
        ctx = (Context)ctx.lookup("java:comp/env");
        DataSource ds = (DataSource)ctx.lookup(dbcc.JNDIName);
        conn = ds.getConnection();
      } else if (Config.isJboss()) {
        Hashtable environment = new Hashtable();
        environment.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        environment.put("java.naming.factory.url.pkgs", "org.jboss.naming.client ");
        environment.put("java.naming.provider.url", "jnp://127.0.0.1:1099");
        ctx = new InitialContext(environment);
        DataSource ds = (DataSource)ctx.lookup("java:" + dbcc.JNDIName);
        conn = ds.getConnection();
      } else {
        DataSource ds = (DataSource)ctx.lookup(dbcc.JNDIName);
        conn = ds.getConnection();
      }
      IDBType t = (IDBType)DBTypeService.getInstance().get(dbcc.DBType);
      DBConn dbconn = new DBConn();
      dbconn.Conn = conn;
      dbconn.DBConfig = dbcc;
      dbconn.ConnID = connID;
      if (!jndiMap.containsKey(conn.toString())) {
        t.afterConnectionCreate(dbconn);
        jndiMap.put(conn.toString(), "");
      }
      return dbconn;
    } catch (Exception e) {
      e.printStackTrace();
      LogUtil.warn("Find JNDI connection pool failed:" + e.getMessage());
      DBConn.removeConnID(connID);
    }
    return null;
  }

  public static DBConn createConnection(DBConnConfig dbcc, boolean bLongTimeFlag) throws Exception {
    Connection conn = null;
    if (dbcc.isJNDIPool) {
      return getJNDIPoolConnection(dbcc);
    }
    IDBType t = (IDBType)DBTypeService.getInstance().get(dbcc.DBType);
    if (t == null) {
      LogUtil.error("Database type is not supported:" + dbcc.DBType);
    }
    conn = t.createConnection(dbcc);

    DBConn dbconn = new DBConn();
    dbconn.Conn = conn;
    dbconn.isUsing = true;
    dbconn.LongTimeFlag = bLongTimeFlag;
    dbconn.LastApplyTime = System.currentTimeMillis();
    dbconn.DBConfig = dbcc;
    dbconn.ConnID = DBConn.getConnID();
    return dbconn;
  }

  private void setCaller(DBConn conn) {
    conn.CallerStackTrace = new Throwable().getStackTrace();
  }

  private String getCallerStack(DBConn conn) {
    StackTraceElement[] trace = conn.CallerStackTrace;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < trace.length; i++) {
      sb.append("\tat " + trace[i]);
    }
    return sb.toString();
  }
}