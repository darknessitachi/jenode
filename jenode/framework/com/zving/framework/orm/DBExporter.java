package com.zving.framework.orm;

import com.zving.framework.Config;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.ui.control.LongTimeTask;
import com.zving.framework.utility.BufferedRandomAccessFile;
import com.zving.framework.utility.FileUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.NumberUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import com.zving.framework.utility.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBExporter
{
  public static final int PageSize = 500;
  private DataAccess da;
  private BufferedRandomAccessFile braf;
  private LongTimeTask task;
  private static final String ZDMVersion1 = "2";
  private static String CurrentVersion = "2";
  private ArrayList<ZDTParser.ZDTTableInfo> Tables;

  public void setTask(LongTimeTask task)
  {
    this.task = task;
  }

  public void setTables(ArrayList<ZDTParser.ZDTTableInfo> tables) {
    this.Tables = tables;
  }

  public void exportDB(String file) {
    exportDB(file, "", null);
  }

  public void exportDB(String file, ArrayList<String> tableList) {
    exportDB(file, "", tableList);
  }

  public void exportDB(String file, String poolName, ArrayList<String> tableList) {
    DBConn conn = DBConnPool.getConnection(poolName);
    try {
      exportDB(file, conn, tableList);
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void exportDB(String file, DBConn conn, ArrayList<String> list)
  {
    String dir = FileUtil.normalizePath(file);
    File dFile = new File(dir.substring(0, dir.lastIndexOf("/") + 1));
    if (!dFile.exists()) {
      dFile.mkdirs();
    }
    FileUtil.delete(file);
    this.da = new DataAccess(conn);
    try {
      this.braf = new BufferedRandomAccessFile(file, "rw");

      byte[] bs = CurrentVersion.getBytes();
      this.braf.write(NumberUtil.toBytes(bs.length));
      this.braf.write(bs);

      if (this.Tables == null) {
        this.Tables = getTableListFromClass();
      }

      for (int i = 0; i < this.Tables.size(); i++)
        try {
          ZDTParser.ZDTTableInfo table = (ZDTParser.ZDTTableInfo)this.Tables.get(i);
          if (this.task != null) {
            this.task.setPercent(new Double(i * 100.0D / this.Tables.size()).intValue());
            this.task.setCurrentInfo("Exporting table " + table.Name);
          }
          String tableCode = ((ZDTParser.ZDTTableInfo)this.Tables.get(i)).Name;
          if ((list == null) || (list.contains(tableCode))) {
            transferOneTable(tableCode, table.IndexInfo, table.Columns);
            transferOneTable("B" + tableCode, null, DAOUtil.addBackupColumn(table.Columns));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (this.braf != null) {
        try {
          this.braf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      this.da.close();
    }
  }

  private ArrayList<ZDTParser.ZDTTableInfo> getTableListFromClass() {
    String[] arr = DAOUtil.getAllDAOClassName();
    ArrayList list = new ArrayList();
    for (String daoClassName : arr) {
      ZDTParser.ZDTTableInfo ti = new ZDTParser.ZDTTableInfo();
      try {
        DAO dao = (DAO)Class.forName(daoClassName).newInstance();
        String name = DAOUtil.getTableCode(dao);
        ti.Name = name;
        ti.IndexInfo = dao.indexInfo();
        ti.Columns = dao.columns();
        list.add(ti);
      } catch (Exception e) {
        LogUtil.warn("DAO's target table not found:" + daoClassName);
      }
    }
    return list;
  }

  private void transferOneTable(String name, String indexInfo, DAOColumn[] scs) throws Exception {
    try {
      StringBuilder columns = new StringBuilder();
      for (DAOColumn sc : scs) {
        columns.append(",").append(sc.getColumnName());
      }
      QueryBuilder qb = new QueryBuilder("select " + columns.toString().trim().substring(1) + " from " + name, new Object[0]);
      int count = Integer.parseInt(this.da.executeOneValue(new QueryBuilder("select count(1) from " + name, new Object[0])).toString());
      int i = 0;
      do { do { DataTable dt = this.da.executePagedDataTable(qb, 500, i);

          if (scs.length != dt.getColumnCount()) {
            throw new RuntimeException("Schema not match table:" + name);
          }

          byte[] bs = name.getBytes();
          this.braf.write(NumberUtil.toBytes(bs.length));
          this.braf.write(bs);

          bs = getColumnString(scs).getBytes();
          this.braf.write(NumberUtil.toBytes(bs.length));
          this.braf.write(bs);

          if (ObjectUtil.empty(indexInfo)) {
            indexInfo = "_ZVING_NULL";
          }
          bs = indexInfo.getBytes();
          this.braf.write(NumberUtil.toBytes(bs.length));
          this.braf.write(bs);

          bs = getDataTableString(scs, dt).getBytes("UTF-8");
          bs = ZipUtil.zip(bs);
          this.braf.write(NumberUtil.toBytes(bs.length));
          this.braf.write(bs);

          i++; } while (i * 500 < count); if (i != 0) break;  } while (count == 0);
    }
    catch (Exception e)
    {
      LogUtil.warn("Table not found:" + name + ";" + e.getMessage());
      return;
    }
  }

  public static final String getColumnString(DAOColumn[] scs) {
    StringBuilder sb = new StringBuilder();
    DAOColumn[] arrayOfDAOColumn = scs; int j = scs.length; for (int i = 0; i < j; i++) { DAOColumn sc = arrayOfDAOColumn[i];
      sb.append(sc.getColumnName());
      sb.append("\t");
      sb.append(sc.getColumnType());
      sb.append("\t");
      sb.append(sc.getLength());
      sb.append("\t");
      sb.append(sc.getPrecision());
      sb.append("\t");
      sb.append(sc.isMandatory());
      sb.append("\t");
      sb.append(sc.isPrimaryKey());
      sb.append("\n");
    }
    return sb.toString().trim();
  }

  public static final String getDataTableString(DAOColumn[] scs, DataTable dt) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dt.getRowCount(); i++) {
      for (int j = 0; j < dt.getColumnCount(); j++) {
        if (j != 0) {
          sb.append("\t");
        }
        if (scs[j].getColumnType() == 2) {
          sb.append(StringUtil.javaEncode(StringUtil.base64Encode((byte[])dt.get(i, j))));
        } else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[j].getColumnType()), Integer.valueOf(1), Integer.valueOf(10) })) {
          String v = dt.getString(i, j);
          if (v == null) {
            sb.append("null");
          } else {
            sb.append("\"");
            sb.append(StringUtil.javaEncode(v));
            sb.append("\"");
          }
        } else {
          sb.append(dt.getString(i, j));
        }
      }
      sb.append("\n");
    }
    return sb.toString().trim();
  }

  public static void main(String[] args) {
    if (ObjectUtil.empty(args)) {
      return;
    }
    Config.setPluginContext(true);
    new DBExporter().exportDB(args[0]);
  }
}