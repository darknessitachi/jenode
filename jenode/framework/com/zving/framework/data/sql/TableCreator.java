package com.zving.framework.data.sql;

import com.zving.framework.data.DBConn;
import com.zving.framework.data.DataAccess;
import com.zving.framework.data.QueryBuilder;
import com.zving.framework.data.QueryException;
import com.zving.framework.data.Transaction;
import com.zving.framework.data.command.CreateIndexCommand;
import com.zving.framework.data.command.CreateTableCommand;
import com.zving.framework.data.command.DropIndexCommand;
import com.zving.framework.data.command.DropTableCommand;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.data.exception.DatabaseException;
import com.zving.framework.orm.DAO;
import com.zving.framework.orm.DAOColumn;
import com.zving.framework.orm.DAOUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class TableCreator
{
  private ArrayList<String> list = new ArrayList();
  private String DBType;
  private IDBType db;

  public TableCreator(String dbType)
  {
    this.DBType = dbType;
    this.db = ((IDBType)DBTypeService.getInstance().get(this.DBType));
  }

  public void createTable(DAO<?> dao) throws Exception {
    createTable(dao, true);
  }

  public void createTable(DAO<?> dao, boolean dropBeforeCreate) throws Exception {
    createTable(DAOUtil.getColumns(dao), DAOUtil.getTableCode(dao), true);
  }

  public void createTable(DAOColumn[] scs, String tableCode) throws Exception {
    createTable(scs, tableCode, true);
  }

  public void createTable(DAOColumn[] scs, String tableCode, boolean dropBeforeCreate) throws Exception {
    if (!dropBeforeCreate) {
      try {
        new QueryBuilder("select count(1) from " + tableCode, new Object[0]).executeInt();
      }
      catch (QueryException e) {
        this.list.add(createTable(scs, tableCode, this.DBType));
      }
    } else {
      dropTable(tableCode);
      this.list.add(createTable(scs, tableCode, this.DBType));
    }
  }

  public static String createTable(DAOColumn[] scs, String tableCode, String DBType) throws Exception {
    IDBType db = (IDBType)DBTypeService.getInstance().get(DBType);
    CreateTableCommand c = new CreateTableCommand();
    c.Table = tableCode;
    c.Columns = ObjectUtil.toList(scs);
    return db.toSQLArray(c)[0];
  }

  public void executeAndClear()
  {
    Transaction tran = new Transaction();
    executeAndClear(tran);
    tran.commit();
  }

  public void executeAndClear(Transaction tran)
  {
    for (int i = 0; i < this.list.size(); i++) {
      QueryBuilder qb = new QueryBuilder(((String)this.list.get(i)).toString(), new Object[0]);
      tran.add(qb);
    }
    this.list.clear();
  }

  public void executeAndClear(DBConn conn)
  {
    DataAccess da = new DataAccess(conn);
    for (int i = 0; i < this.list.size(); i++) {
      QueryBuilder qb = new QueryBuilder(((String)this.list.get(i)).toString(), new Object[0]);
      try {
        da.executeNoQuery(qb);
      } catch (DatabaseException e) {
        if (qb.getSQL().startsWith("drop table")) {
          String table = qb.getSQL();
          table = table.substring(table.indexOf(" ", 8)).trim();

          LogUtil.info("Can't drop table，may be not exist：" + table);
        } else if (qb.getSQL().indexOf("drop index ") >= 0) {
          LogUtil.info("Can't drop index: " + e.getMessage().trim() + ", SQL=" + qb.getSQL());
        } else if (qb.getSQL().indexOf("create index ") >= 0) {
          LogUtil.warn("Create index failed: " + e.getMessage().trim() + ", SQL=" + qb.getSQL());
        } else {
          LogUtil.warn(qb.getSQL());
          e.printStackTrace();
        }
      }
    }
    this.list.clear();
  }

  public String[] getSQLArray()
  {
    String[] arr = new String[this.list.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = ((String)this.list.get(i)).toString();
    }
    return arr;
  }

  public String getAllSQL()
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.list.size(); i++) {
      sb.append((String)this.list.get(i));
      sb.append(this.db.getSQLSperator());
    }
    return sb.toString();
  }

  public void dropTable(String tableCode)
  {
    DropTableCommand c = new DropTableCommand();
    c.Table = tableCode;
    for (String sql : this.db.toSQLArray(c))
      this.list.add(sql);
  }

  public static String dropTable(String tableCode, String dbType)
  {
    DropTableCommand c = new DropTableCommand();
    c.Table = tableCode;
    IDBType db = (IDBType)DBTypeService.getInstance().get(dbType);
    return db.toSQLArray(c)[0];
  }

  public void createIndexes(String tableCode, String indexInfo) {
    for (String sql : createIndexes(tableCode, indexInfo, true, this.db))
      this.list.add(sql);
  }

  public static List<String> createIndexes(String tableCode, String indexInfo, boolean dropFirst, IDBType db)
  {
    if (ObjectUtil.empty(indexInfo)) {
      return new ArrayList();
    }
    ArrayList list = new ArrayList();
    for (String str : StringUtil.splitEx(indexInfo, ";"))
      if (!ObjectUtil.empty(str))
      {
        int index = str.indexOf(":");
        if (index >= 1)
        {
          String name = str.substring(0, index);
          str = str.substring(index + 1);
          ArrayList columns = new ArrayList();
          for (String c : StringUtil.splitEx(str, ",")) {
            columns.add(c);
          }
          if (dropFirst) {
            DropIndexCommand dic = new DropIndexCommand();
            dic.Table = tableCode;
            dic.Name = name;
            for (String sql : db.toSQLArray(dic)) {
              list.add(sql);
            }
          }
          CreateIndexCommand cic = new CreateIndexCommand();
          cic.Table = tableCode;
          cic.Name = name;
          cic.Columns = columns;
          for (String sql : db.toSQLArray(cic))
            list.add(sql);
        }
      }
    return list;
  }
}