package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONArray;
import com.zving.framework.json.JSONObject;
import com.zving.framework.orm.DAOColumn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CreateTableCommand
  implements IDBCommand
{
  public String Table;
  public List<DAOColumn> Columns;
  public static final String Prefix = "CreateTable:";

  public String getPrefix()
  {
    return "CreateTable:";
  }

  public void parse(String ddl) {
    ddl = ddl.substring("CreateTable:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    JSONArray cs = (JSONArray)map.get("Columns");
    this.Columns = new ArrayList();
    for (Iterator localIterator = cs.iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
      JSONArray arr = (JSONArray)obj;
      DAOColumn sc = new DAOColumn(arr.get(0).toString(), Integer.parseInt(arr.get(1).toString()), Integer.parseInt(arr.get(2)
        .toString()), Integer.parseInt(arr.get(3).toString()), "true".equals(arr.get(4).toString()), "true".equals(arr.get(5)
        .toString()));
      this.Columns.add(sc); }
  }

  public String toJSON()
  {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    Object[][] columns = new Object[this.Columns.size()][6];
    int i = 0;
    for (DAOColumn sc : this.Columns) {
      Object[] arr = { sc.getColumnName(), Integer.valueOf(sc.getColumnType()), Integer.valueOf(sc.getLength()), Integer.valueOf(sc.getPrecision()), Boolean.valueOf(sc.isMandatory()), 
        Boolean.valueOf(sc.isPrimaryKey()) };
      columns[(i++)] = arr;
    }
    map.put("Columns", columns);
    return "CreateTable:" + JSON.toJSONString(map);
  }

  public String[] getDefaultSQLArray(String dbType) {
    StringBuilder sb = new StringBuilder();
    sb.append("create table " + this.Table + "(\n");
    StringBuilder ksb = new StringBuilder();
    IDBType t = (IDBType)DBTypeService.getInstance().get(dbType);
    for (int i = 0; i < this.Columns.size(); i++) {
      DAOColumn sc = (DAOColumn)this.Columns.get(i);
      if (i != 0) {
        sb.append(",\n");
      }
      sb.append("\t" + sc.getColumnName() + " ");
      String sqlType = t.toSQLType(sc.getColumnType(), sc.getLength(), sc.getPrecision());
      sb.append(sqlType + " ");
      if (sc.isMandatory()) {
        sb.append("not null");
      }
      if (sc.isPrimaryKey()) {
        if (ksb.length() == 0) {
          String pkName = this.Table;
          if (pkName.length() > 15) {
            pkName = pkName.substring(0, 15);
          }
          ksb.append("\t" + t.getPKNameFragment(this.Table) + " (");
        } else {
          ksb.append(",");
        }
        ksb.append(sc.getColumnName());
      }
    }
    if (ksb.length() != 0) {
      ksb.append(")");
      sb.append(",\n" + ksb);
    }
    sb.append("\n)");
    return new String[] { sb.toString() };
  }
}