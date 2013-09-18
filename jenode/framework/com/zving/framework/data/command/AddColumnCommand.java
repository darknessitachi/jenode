package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class AddColumnCommand
  implements IDBCommand
{
  public String Column;
  public int DataType;
  public String Table;
  public int Length;
  public int Precision;
  public boolean Mandatory;
  public static final String Prefix = "AddColumn:";

  public String[] getDefaultSQLArray(String dbType)
  {
    IDBType db = (IDBType)DBTypeService.getInstance().get(dbType);
    String sql = "alter table " + this.Table + " add column " + this.Column + " " + db.toSQLType(this.DataType, this.Length, this.Precision);
    if (this.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("AddColumn:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Column = map.getString("Column");
    this.DataType = map.getInt("DataType");
    this.Table = map.getString("Table");
    this.Length = map.getInt("Length");
    this.Precision = map.getInt("Precision");
    this.Mandatory = map.getBoolean("Mandatory");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Column", this.Column);
    map.put("DataType", Integer.valueOf(this.DataType));
    map.put("Table", this.Table);
    map.put("Length", Integer.valueOf(this.Length));
    map.put("Precision", Integer.valueOf(this.Precision));
    map.put("Mandatory", Boolean.valueOf(this.Mandatory));
    return "AddColumn:" + JSON.toJSONString(map);
  }

  public String getPrefix() {
    return "AddColumn:";
  }
}