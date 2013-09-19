package com.zving.framework.data;

import com.zving.framework.collection.Executor;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlockingTransaction extends Transaction
{
  private boolean isExistsOpeningOperate = false;

  private static ThreadLocal<Object> current = new ThreadLocal();
  private DBConn conn;

  public BlockingTransaction()
  {
    if (this.dataAccess == null) {
      this.conn = DBConnPool.getConnection();
      this.conn.isBlockingTransactionStarted = true;
      this.dataAccess = new DataAccess(this.conn);
      try {
        this.dataAccess.setAutoCommit(false);
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
      bindTransactionToThread();
    }
  }

  public BlockingTransaction(String poolName) {
    this.dataAccess = new DataAccess(DBConnPool.getConnection(poolName));
    this.conn = this.dataAccess.getConnection();
    this.conn.isBlockingTransactionStarted = true;
    try {
      this.dataAccess.setAutoCommit(false);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    bindTransactionToThread();
  }

  public BlockingTransaction(DataAccess da) {
    this.dataAccess = da;
    this.conn = this.dataAccess.getConnection();
    this.conn.isBlockingTransactionStarted = true;
    try {
      this.dataAccess.setAutoCommit(false);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    bindTransactionToThread();
  }

  public BlockingTransaction setDataAccess(DataAccess dAccess)
  {
    if ((this.dataAccess != null) && (!this.outerConnFlag)) {
      throw new RuntimeException("setDataAccess() must before any add()");
    }
    super.setDataAccess(dAccess);
    return this;
  }

  public BlockingTransaction add(QueryBuilder qb)
  {
    executeWithBlockedConnection(qb, 7, true);
    return this;
  }

  public BlockingTransaction add(DAO<?> dao, int type)
  {
    executeWithBlockedConnection(dao, type, true);
    return this;
  }

  public BlockingTransaction add(DAOSet<?> set, int type)
  {
    executeWithBlockedConnection(set, type, true);
    return this;
  }

  public BlockingTransaction addWithException(QueryBuilder qb)
    throws Exception
  {
    executeWithBlockedConnection(qb, 7, false);
    return this;
  }

  public BlockingTransaction addWithException(DAO<?> dao, int type)
    throws Exception
  {
    executeWithException(dao, type);
    return this;
  }

  public BlockingTransaction addWithException(DAOSet<?> set, int type)
    throws Exception
  {
    executeWithException(set, type);
    return this;
  }

  private void executeWithBlockedConnection(Object obj, int type, boolean rollBackFlag)
  {
    try
    {
      executeObject(obj, type);
      this.isExistsOpeningOperate = true;
    } catch (SQLException e) {
      if ((!this.outerConnFlag) && (rollBackFlag)) {
        try {
          this.dataAccess.rollback();
          this.conn.isBlockingTransactionStarted = false;
        } catch (DatabaseException e1) {
          e1.printStackTrace();
          try
          {
            this.dataAccess.close();
          } catch (DatabaseException e1x) {
            e1x.printStackTrace();
          }
        }
        finally
        {
          try
          {
            this.dataAccess.close();
          } catch (DatabaseException e1) {
            e1.printStackTrace();
          }
        }
      }
      throw new RuntimeException(e);
    }
  }

  private void executeWithException(Object obj, int type)
    throws Exception
  {
    executeObject(obj, type);
    this.isExistsOpeningOperate = true;
  }

  public boolean commit()
  {
    return commit(true);
  }

  public boolean commit(boolean setAutoCommitStatus)
  {
    if (this.dataAccess != null) {
      try {
        this.dataAccess.commit();
      } catch (DatabaseException e) {
        e.printStackTrace();
        this.exceptionMessage = e.getMessage();
        if (!this.outerConnFlag) {
          try {
            this.dataAccess.rollback();
          } catch (DatabaseException e1) {
            e1.printStackTrace();
          }
        }
        return false;
      } finally {
        try {
          if ((!this.outerConnFlag) || (setAutoCommitStatus))
            this.dataAccess.setAutoCommit(true);
        }
        catch (DatabaseException e1) {
          e1.printStackTrace();
        }
        if (!this.outerConnFlag) {
          try {
            this.conn.isBlockingTransactionStarted = false;
            this.dataAccess.getConnection().close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
        this.isExistsOpeningOperate = false;
        current.set(null);
      }
      for (int i = 0; i < this.executorList.size(); i++) {
        Executor executor = (Executor)this.executorList.get(i);
        executor.execute();
      }
    }
    return true;
  }

  public void rollback()
  {
    if (this.dataAccess != null) {
      try {
        this.dataAccess.rollback();
        this.isExistsOpeningOperate = false;
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
      try {
        this.conn.isBlockingTransactionStarted = false;
        this.dataAccess.getConnection().close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      current.set(null);
    }
  }

  private void bindTransactionToThread()
  {
    Object obj = current.get();
    if (obj == null)
      current.set(this);
    else
      throw new RuntimeException("One thread cann't have two BlockingTransaction!");
  }

  public static void clearTransactionBinding()
  {
    Object obj = current.get();
    if (obj == null) {
      return;
    }
    BlockingTransaction bt = (BlockingTransaction)obj;
    if (bt.isExistsOpeningOperate) {
      bt.rollback();
    }
    current.set(null);
  }

  public static DBConn getCurrentThreadConnection()
  {
    Object obj = current.get();
    if (obj == null) {
      return null;
    }
    BlockingTransaction bt = (BlockingTransaction)obj;
    if ((bt.dataAccess == null) || (bt.dataAccess.conn == null)) {
      return null;
    }
    return bt.dataAccess.getConnection();
  }
}