package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONArray;
import com.zving.framework.json.JSONObject;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.util.List;

public class CreateIndexCommand
  implements IDBCommand
{
  public String Table;
  public String Name;
  public List<String> Columns;
  public static final String Prefix = "CreateIndex:";

  public String getPrefix()
  {
    return "CreateIndex:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    StringBuilder sb = new StringBuilder();
    sb.append("create index ");
    sb.append(this.Name);
    sb.append(" on ");
    sb.append(this.Table);
    sb.append(" (");
    boolean first = true;
    for (String column : this.Columns)
      if (!StringUtil.isEmpty(column))
      {
        if (!first) {
          sb.append(",");
        }
        sb.append(column);
        first = false;
      }
    sb.append(")");
    return new String[] { sb.toString() };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("CreateIndex:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Columns = ObjectUtil.toStringList((JSONArray)map.get("Columns"));
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Columns", this.Columns);
    return "CreateIndex:" + JSON.toJSONString(map);
  }
}