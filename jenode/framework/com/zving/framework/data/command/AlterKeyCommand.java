package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONArray;
import com.zving.framework.json.JSONObject;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.util.List;

public class AlterKeyCommand
  implements IDBCommand
{
  public String Table;
  public List<String> Columns;
  public static final String Prefix = "AlterKey:";

  public String getPrefix()
  {
    return "AlterKey:";
  }

  public void parse(String ddl) {
    ddl = ddl.substring("AlterKey:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Columns = ObjectUtil.toStringList((JSONArray)map.get("Columns"));
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Columns", this.Columns);
    return "AlterKey:" + JSON.toJSONString(map);
  }

  public String[] getDefaultSQLArray(String dbType) {
    String sql1 = null; String sql2 = null;
    if ("MSSQL".equals(dbType))
      sql1 = "alter table " + this.Table + " drop constraint PK_" + this.Table;
    else {
      sql1 = "alter table " + this.Table + " drop primary key";
    }
    IDBType db = (IDBType)DBTypeService.getInstance().get(dbType);
    sql2 = "alter table " + this.Table + " add " + db.getPKNameFragment(this.Table) + " (" + StringUtil.join(this.Columns) + ")";
    return new String[] { sql1, sql2 };
  }
}