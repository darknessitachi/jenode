package com.zving.framework.data.dbtype;

import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.data.sql.SelectSQLParser;
import java.sql.SQLException;
import java.sql.Statement;

public class Sybase extends SQLServer2005
{
  public static final String ID = "SYBASE";

  public String getID()
  {
    return "SYBASE";
  }

  public String getName() {
    return "Sybase";
  }

  public boolean isFullSupport() {
    return true;
  }

  public String getJdbcUrl(DBConnConfig dcc) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:sybase:Tds:");
    sb.append(dcc.DBServerAddress);
    sb.append(":");
    sb.append(dcc.DBPort);
    sb.append("/");
    sb.append(dcc.DBName);
    return sb.toString();
  }

  public void afterConnectionCreate(DBConn conn) {
    try {
      Statement stmt = conn.createStatement(1005, 1008);
      stmt.execute("set textsize 20971520");
      stmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public int getDefaultPort() {
    return 5000;
  }

  public String getPagedSQL(DBConn conn, QueryBuilder qb, int pageSize, int pageIndex) {
    StringBuilder sb = new StringBuilder();
    String sql = qb.getSQL();
    SelectSQLParser sp = new SelectSQLParser();
    try {
      sp.setSQL(sql);
      sp.parse();
    } catch (Exception e) {
      e.printStackTrace();
    }
    sb.append(sp.getSybasePagedSQL(pageSize, pageIndex, DBConn.getConnID()));
    return sb.toString();
  }

  public String getDriverClass() {
    return "com.sybase.jdbc3.jdbc.SybDriver";
  }
}