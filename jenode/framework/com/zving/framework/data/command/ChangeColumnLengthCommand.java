package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;

public class ChangeColumnLengthCommand
  implements IDBCommand
{
  public String Column;
  public int DataType;
  public int Length;
  public int Precision;
  public boolean Mandatory;
  public String Table;
  public static final String Prefix = "ChangeColumnLength:";

  public String getPrefix()
  {
    return "ChangeColumnLength:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    IDBType db = (IDBType)DBTypeService.getInstance().get(dbType);
    String sql = "alter table " + this.Table + " modify column " + this.Column + " " + db.toSQLType(this.DataType, this.Length, this.Precision);
    if (this.Mandatory) {
      sql = sql + " not null";
    }
    return new String[] { sql };
  }

  public void parse(String ddl) {
    ddl = ddl.substring("ChangeColumnLength:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Column = map.getString("Column");
    this.DataType = map.getInt("DataType");
    this.Length = map.getInt("Length");
    this.Precision = map.getInt("Precision");
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("DataType", Integer.valueOf(this.DataType));
    map.put("Column", this.Column);
    map.put("Length", Integer.valueOf(this.Length));
    map.put("Precision", Integer.valueOf(this.Precision));
    return "ChangeColumnLength:" + JSON.toJSONString(map);
  }
}