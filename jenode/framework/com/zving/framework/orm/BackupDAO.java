package com.zving.framework.orm;

import com.zving.framework.Config;
import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.core.bean.BeanDescription;
import com.zving.framework.core.bean.BeanManager;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.util.Date;

public class BackupDAO<T extends DAO<T>> extends DAO<BackupDAO<T>>
{
  private static final long serialVersionUID = 1L;
  private Class<T> clazz;
  private T dao;
  private String BackupNo;
  private String BackupOperator;
  private Date BackupTime;
  private String BackupMemo;

  public BackupDAO(Class<T> c)
  {
    try
    {
      this.clazz = c;
      this.dao = ((T)this.clazz.newInstance());
      DAOMetadata m = DAOMetadataManager.getMetadata(this.clazz);
      DAOColumn[] columns = DAOUtil.addBackupColumn(m.getColumns());
      String table = "B" + m.getTable();
      CaseIgnoreMapx map = m.getBeanProperties();
      BeanDescription bean = BeanManager.getBeanDescription(getClass());
      map.put("BackupNo", bean.getProperty("BackupNo"));
      map.put("BackupOperator", bean.getProperty("BackupOperator"));
      map.put("BackupTime", bean.getProperty("BackupTime"));
      map.put("BackupMemo", bean.getProperty("BackupMemo"));
      this.meta = new DAOMetadata(table, columns, m.getIndexes(), map);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public DAOMetadata metadata() {
    return this.meta;
  }

  public DAOMetadata getMetadata() {
    return this.meta;
  }

  public T getDAO() {
    return this.dao;
  }

  public BackupDAO<T> newInstance()
  {
    return new BackupDAO(this.clazz);
  }

  public DAOSet<BackupDAO<T>> newSet()
  {
    return new DAOSet();
  }

  public String getBackupNo() {
    return this.BackupNo;
  }

  public void setBackupNo(String backupNo) {
    if (backupNo != null) {
      int len = StringUtil.lengthEx(backupNo, Config.getGlobalCharset().equals("UTF-8"));
      if (len > 15) {
        throw new RuntimeException("Data is too long, max is 15,actual is " + len + ":" + backupNo);
      }
    }
    this.BackupNo = backupNo;
  }

  public String getBackupOperator() {
    return this.BackupOperator;
  }

  public void setBackupOperator(String backupOperator) {
    if (backupOperator != null) {
      int len = StringUtil.lengthEx(backupOperator, Config.getGlobalCharset().equals("UTF-8"));
      if (len > 50) {
        throw new RuntimeException("Data is too long, max is 50,actual is " + len + ":" + backupOperator);
      }
    }
    this.BackupOperator = backupOperator;
  }

  public Date getBackupTime() {
    return this.BackupTime;
  }

  public void setBackupTime(Date backupTime) {
    this.BackupTime = backupTime;
  }

  public void setBackupTime(String backupTime) {
    if (backupTime == null) {
      this.BackupTime = null;
      return;
    }
    this.BackupTime = DateUtil.parseDateTime(backupTime);
  }

  public String getBackupMemo() {
    return this.BackupMemo;
  }

  public void setBackupMemo(String backupMemo) {
    if (backupMemo != null) {
      int len = StringUtil.lengthEx(backupMemo, Config.getGlobalCharset().equals("UTF-8"));
      if (len > 200) {
        throw new DatabaseException("Data is too long, max is 200,actual is " + len + ":" + backupMemo);
      }
    }
    this.BackupMemo = backupMemo;
  }

  public void setV(String columnName, Object v)
  {
    _init();
    if (ObjectUtil.in(new Object[] { columnName, "BackupNo", "BackupOperator", "BackupMemo", "BackupTime" }))
      this.meta.setV(this, columnName, v);
    else
      this.meta.setV(this.dao, columnName, v);
  }

  public Object getV(String columnName)
  {
    _init();
    if (ObjectUtil.in(new Object[] { columnName, "BackupNo", "BackupOperator", "BackupMemo", "BackupTime" })) {
      return this.meta.getV(this, columnName);
    }
    return this.meta.getV(this.dao, columnName);
  }

  public String backup()
  {
    throw new DAOException("Cann't backup a BackupDAO");
  }

  public String backup(String backupOperator, String backupMemo) {
    throw new DAOException("Cann't backup a BackupDAO");
  }

  public boolean deleteAndBackup() {
    throw new DAOException("Cann't backup a BackupDAO");
  }

  public boolean deleteAndBackup(String backupOperator, String backupMemo) {
    throw new DAOException("Cann't backup a BackupDAO");
  }
}