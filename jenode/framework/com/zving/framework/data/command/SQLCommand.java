package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class SQLCommand
  implements IDBCommand
{
  public String SQL;
  public static final String Prefix = "SQL:";

  public String getPrefix()
  {
    return "SQL:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    return new String[] { this.SQL };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("SQL:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.SQL = map.getString("SQL");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("SQL", this.SQL);
    return "SQL:" + JSON.toJSONString(map);
  }
}