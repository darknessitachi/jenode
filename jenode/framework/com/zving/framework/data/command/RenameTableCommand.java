package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class RenameTableCommand
  implements IDBCommand
{
  public static final String Prefix = "RenameTable:";
  public String Table;
  public String NewTable;

  public String getPrefix()
  {
    return "RenameTable:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    return new String[] { "rename table " + this.Table + " to " + this.NewTable };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("RenameTable:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.NewTable = map.getString("NewTable");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("NewTable", this.NewTable);
    return "RenameTable:" + JSON.toJSONString(map);
  }
}