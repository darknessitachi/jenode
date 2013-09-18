package com.zving.framework.data;

import com.zving.framework.collection.Mapx;

public class DBConnConfig
{
  public static final String ORACLE = "ORACLE";
  public static final String DB2 = "DB2";
  public static final String MYSQL = "MYSQL";
  public static final String MSSQL = "MSSQL";
  public static final String MSSQL2000 = "MSSQL2000";
  public static final String SYBASE = "SYBASE";
  public static final String HSQLDB = "HSQLDB";
  public String JNDIName = null;

  public boolean isJNDIPool = false;

  public int MaxConnCount = 1000;

  public int InitConnCount = 5;
  public int ConnCount;
  public int MaxConnUsingTime = 300000;

  public int RefershPeriod = 30000;
  public String DBType;
  public String DBServerAddress;
  public int DBPort;
  public String ConnectionURL;
  public String DBName;
  public String DBUserName;
  public String DBPassword;
  public String TestTable;
  public String PoolName;
  public String Charset;
  public boolean isLatin1Charset;
  public Mapx<String, String> Properties;

  public boolean isOracle()
  {
    return this.DBType.equalsIgnoreCase("ORACLE");
  }

  public boolean isDB2() {
    return this.DBType.equalsIgnoreCase("DB2");
  }

  public boolean isMysql() {
    return this.DBType.equalsIgnoreCase("MYSQL");
  }

  public boolean isSQLServer() {
    return this.DBType.equalsIgnoreCase("MSSQL");
  }

  public boolean isSQLServer2000() {
    return this.DBType.equalsIgnoreCase("MSSQL2000");
  }

  public boolean isSybase() {
    return this.DBType.equalsIgnoreCase("SYBASE");
  }

  public boolean isHsqldb() {
    return this.DBType.equalsIgnoreCase("HSQLDB");
  }
}