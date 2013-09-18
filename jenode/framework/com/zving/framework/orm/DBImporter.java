package com.zving.framework.orm;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DBConn;
import com.zving.framework.data.DBConnConfig;
import com.zving.framework.data.DBConnPool;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.data.sql.TableCreator;
import com.zving.framework.ui.control.LongTimeTask;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBImporter
{
  private DataAccess da;
  private LongTimeTask task;

  public void setTask(LongTimeTask task)
  {
    this.task = task;
  }

  public String getSQL(String file, String dbtype) {
    TableCreator tc = new TableCreator(dbtype);
    try {
      ZDTParser parser = new ZDTParser(file);
      parser.parse();
      for (ZDTParser.ZDTTableInfo ti : parser.getTables()) {
        tc.createTable(ti.Columns, ti.Name);
        tc.createIndexes(ti.Name, ti.IndexInfo);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return tc.getAllSQL();
  }

  public void importDB(String file) {
    importDB(file, "", null);
  }

  public void importDB(String file, List<String> tableList) {
    importDB(file, "", tableList);
  }

  public boolean importDB(String file, String poolName, List<String> tableList) {
    DBConn conn = DBConnPool.getConnection(poolName);
    try {
      return importDB(file, conn, true, tableList);
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean importDB(String file, DBConn conn, boolean dropBeforeCreate, List<String> tableList) {
    this.da = new DataAccess(conn);
    try {
      ZDTParser parser = new ZDTParser(file);
      parser.parse();
      Mapx map = new Mapx();
      TableCreator tc = new TableCreator(this.da.getConnection().getDBConfig().DBType);
      int j = 0;
      for (ZDTParser.ZDTTableInfo ti : parser.getTables())
        if ((tableList == null) || (tableList.contains(ti.Name)))
        {
          if (!map.containsKey(ti.Name)) {
            tc.createTable(ti.Columns, ti.Name, dropBeforeCreate);
            tc.executeAndClear(conn);
            map.put(ti.Name, "");
          }
          if (this.task != null) {
            this.task.setPercent(new Double(++j * 100.0D / parser.getTables().size()).intValue());
          }
          for (int i = 0; i < ti.Positions.size(); i++) {
            DataTable dt = parser.getDataTable(ti, i * 500, (i + 1) * 500);
            try {
              if (this.task != null) {
                this.task.setCurrentInfo("Importing table " + ti.Name);
              }
              if (!importDataTable(ti.Columns, dt, ti.Name))
                return false;
            }
            catch (Exception e) {
              LogUtil.warn("Import table failed:" + ti.Name);
              e.printStackTrace();
            }
          }

          if ((ObjectUtil.notEmpty(ti.IndexInfo)) && (!ti.IndexInfo.trim().equals(""))) {
            tc.createIndexes(ti.Name, ti.IndexInfo);
            try {
              tc.executeAndClear(conn);
            } catch (DatabaseException e) {
              LogUtil.warn(e.getMessage());
            }
          }
        }
    } catch (Exception e1) {
      e1.printStackTrace();
      return false;
    }
    return true;
  }

  private boolean importDataTable(DAOColumn[] scs, DataTable dt, String name) throws Exception {
    QueryBuilder qb = new QueryBuilder("insert into " + name + " (", new Object[0]);
    qb.setBatchMode(true);
    for (int i = 0; i < scs.length; i++) {
      if (i != 0) {
        qb.append(",", new Object[0]);
      }
      qb.append(scs[i].getColumnName(), new Object[0]);
    }
    qb.append(") values (", new Object[0]);
    for (int i = 0; i < scs.length; i++) {
      if (i != 0) {
        qb.append(",", new Object[0]);
      }
      qb.append("?", new Object[0]);
    }
    qb.append(")", new Object[0]);
    int j;
    if (this.da.getConnection().getDBConfig().isOracle())
    {
      for (int i = 0; i < dt.getRowCount(); i++) {
        for (j = 0; j < scs.length; j++) {
          Object v = dt.get(i, j);
          if ((scs[j].isMandatory()) && ((v == null) || (v.equals("")))) {
            LogUtil.warn(name + "'s column " + scs[j].getColumnName() + " can't be empty");
            dt.deleteRow(i);
            i--;
            break;
          }
        }
      }
    }
    for (DataRow dr : dt) {
      for (int i = 0; i < scs.length; i++) {
        qb.add(new Object[] { dr.get(i) });
      }
      qb.addBatch();
    }
    this.da.executeNoQuery(qb);
    return true;
  }
}