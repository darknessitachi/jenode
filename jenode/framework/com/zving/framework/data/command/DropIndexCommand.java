package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class DropIndexCommand
  implements IDBCommand
{
  public String Table;
  public String Name;
  public static final String Prefix = "DropIndex:";

  public String getPrefix()
  {
    return "DropIndex:";
  }

  public void parse(String ddl) {
    ddl = ddl.substring("DropIndex:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Name = map.getString("Name");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Name", this.Name);
    return "DropIndex:" + JSON.toJSONString(map);
  }

  public String[] getDefaultSQLArray(String dbType) {
    return new String[] { "drop index " + this.Name };
  }
}