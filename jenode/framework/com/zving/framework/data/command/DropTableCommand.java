package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class DropTableCommand
  implements IDBCommand
{
  public String Table;
  public static final String Prefix = "DropTable:";

  public String getPrefix()
  {
    return "DropTable:";
  }

  public void parse(String ddl) {
    ddl = ddl.substring("DropTable:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    return "DropTable:" + JSON.toJSONString(map);
  }

  public String[] getDefaultSQLArray(String dbType) {
    return new String[] { "drop table " + this.Table };
  }
}