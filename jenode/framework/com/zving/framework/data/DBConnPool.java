package com.zving.framework.data;

import com.zving.framework.Config;
import com.zving.framework.collection.Mapx;
import java.util.Timer;
import java.util.TimerTask;

public class DBConnPool
{
  protected static Mapx<String, DBConnPoolImpl> PoolMap = new Mapx();
  private static Object mutex = new Object();
  private static Timer timer;
  private static TimerTask timerTask;

  public static DBConn getConnection()
  {
    return getConnection(null, false);
  }

  public static DBConn getConnection(boolean bLongTimeFlag) {
    return getConnection(null, bLongTimeFlag);
  }

  public static DBConn getConnection(String poolName) {
    return getConnection(poolName, false);
  }

  public static DBConnConfig getDBConnConfig() {
    return getDBConnConfig(null);
  }

  public static DBConnConfig getDBConnConfig(String poolName) {
    if ((poolName == null) || (poolName.equals(""))) {
      poolName = "Default";
    }
    poolName = poolName + ".";
    DBConnPoolImpl pool = (DBConnPoolImpl)PoolMap.get(poolName);
    if (pool == null) {
      synchronized (mutex) {
        pool = (DBConnPoolImpl)PoolMap.get(poolName);
        if (pool == null) {
          if (Config.getValue("Database." + poolName + "Type") != null) {
            pool = new DBConnPoolImpl(poolName);
            PoolMap.put(poolName, pool);
          } else {
            throw new RuntimeException("DB Connection Pool not found:" + poolName);
          }
        }
      }
    }
    return pool.getDBConnConfig();
  }

  public static DBConn getConnection(String poolName, boolean bLongTimeFlag) {
    return getConnection(poolName, bLongTimeFlag, true);
  }

  public static DBConn getConnection(String poolName, boolean bLongTimeFlag, boolean bCurrentThreadConnectionFlag) {
    if (timer == null) {
      synchronized (mutex) {
        initTimer();
      }
    }
    if ((poolName == null) || (poolName.equals(""))) {
      poolName = "Default";
    }
    poolName = poolName + ".";
    if (bCurrentThreadConnectionFlag) {
      DBConn conn = BlockingTransaction.getCurrentThreadConnection();
      if ((conn != null) && (conn.DBConfig.PoolName.equals(poolName))) {
        return conn;
      }
    }
    DBConnPoolImpl pool = (DBConnPoolImpl)PoolMap.get(poolName);
    if (pool == null) {
      synchronized (mutex) {
        pool = (DBConnPoolImpl)PoolMap.get(poolName);
        if (pool == null) {
          if (Config.getValue("Database." + poolName + "Type") != null) {
            pool = new DBConnPoolImpl(poolName);
            PoolMap.put(poolName, pool);
          } else {
            throw new RuntimeException("DB Connection Pool not found:" + poolName);
          }
        }
      }
    }

    return pool.getConnection(bLongTimeFlag);
  }

  public static Mapx<String, DBConnPoolImpl> getPoolMap() {
    return PoolMap;
  }

  private static void initTimer()
  {
    if (timer != null) {
      return;
    }
    timer = new Timer("DBConnPool KeepAlive Timer", true);
    timerTask = new TimerTask() {
      public void run() {
        if (DBConnPool.PoolMap == null) {
          return;
        }
        for (String name : DBConnPool.PoolMap.keyArray()) {
          DBConnPoolImpl pool = (DBConnPoolImpl)DBConnPool.PoolMap.get(name);
          pool.keepAlive();
        }
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0L, 60000L);
  }

  public static void cancelKeepAliveTimer() {
    if (timer == null) {
      return;
    }
    timerTask.cancel();
    timer.cancel();
  }
}