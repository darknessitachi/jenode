package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class RenameColumnCommand
  implements IDBCommand
{
  public String Column;
  public String NewColumn;
  public int DataType;
  public int Length;
  public int Precision;
  public boolean Mandatory;
  public String Table;
  public static final String Prefix = "RenameColumn:";

  public String getPrefix()
  {
    return "RenameColumn:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    return new String[] { "alter table " + this.Table + " rename column " + this.Column + " to " + this.NewColumn };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("RenameColumn:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Column = map.getString("Column");
    this.NewColumn = map.getString("NewColumn");
    this.DataType = map.getInt("DataType");
    this.Length = map.getInt("Length");
    this.Precision = map.getInt("Precision");
    this.Mandatory = "true".equals(map.getString("Mandatory"));
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Column", this.Column);
    map.put("NewColumn", this.NewColumn);
    map.put("DataType", Integer.valueOf(this.DataType));
    map.put("Length", Integer.valueOf(this.Length));
    map.put("Precision", Integer.valueOf(this.Precision));
    map.put("Mandatory", Boolean.valueOf(this.Mandatory));
    return "RenameColumn:" + JSON.toJSONString(map);
  }
}