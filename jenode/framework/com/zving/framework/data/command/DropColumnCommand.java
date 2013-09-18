package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class DropColumnCommand
  implements IDBCommand
{
  public String Column;
  public String Table;
  public static final String Prefix = "DropColumn:";

  public String getPrefix()
  {
    return "DropColumn:";
  }

  public void parse(String ddl) {
    ddl = ddl.substring("DropColumn:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Column = map.getString("Column");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Column", this.Column);
    return "DropColumn:" + JSON.toJSONString(map);
  }

  public String[] getDefaultSQLArray(String dbType) {
    String sql = "alter table " + this.Table + " drop column " + this.Column;
    return new String[] { sql };
  }
}