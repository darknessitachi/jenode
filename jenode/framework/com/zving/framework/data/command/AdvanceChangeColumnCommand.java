package com.zving.framework.data.command;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.dbtype.DBTypeService;
import com.zving.framework.data.dbtype.IDBType;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONObject;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringFormat;
import java.util.List;

public class AdvanceChangeColumnCommand
  implements IDBCommand
{
  public String Column;
  public int DataType;
  public int Length;
  public int Precision;
  public boolean Mandatory;
  public boolean OldMandatory;
  public int OldDataType;
  public int OldLength;
  public int OldPrecision;
  public String Table;
  public static final String Prefix = "AdvanceChangeColumn:";

  public String getPrefix()
  {
    return "AdvanceChangeColumn:";
  }

  public String[] getDefaultSQLArray(String dbType) {
    IDBType db = (IDBType)DBTypeService.getInstance().get(dbType);

    AddColumnCommand acc = new AddColumnCommand();
    acc.Table = this.Table;
    acc.Column = (this.Column + "_tmp");
    acc.DataType = this.OldDataType;
    acc.Length = this.OldLength;
    acc.Precision = this.OldPrecision;
    acc.Mandatory = false;
    List list = ObjectUtil.toList(db.toSQLArray(acc));

    if (this.OldMandatory) {
      ChangeColumnMandatoryCommand ccm = new ChangeColumnMandatoryCommand();
      ccm.Table = this.Table;
      ccm.Column = this.Column;
      ccm.DataType = this.OldDataType;
      ccm.Length = this.OldLength;
      ccm.Precision = this.OldPrecision;
      ccm.Mandatory = false;
      list.addAll(ObjectUtil.toList(db.toSQLArray(ccm)));
    }

    list.add(StringFormat.format("update ? set ?=?,?=null", new Object[] { this.Table, this.Column + "_tmp", this.Column, this.Column }));

    DropColumnCommand dcc = new DropColumnCommand();
    dcc.Table = this.Table;
    dcc.Column = this.Column;
    list.addAll(ObjectUtil.toList(db.toSQLArray(dcc)));

    acc = new AddColumnCommand();
    acc.Table = this.Table;
    acc.Column = this.Column;
    acc.DataType = this.DataType;
    acc.Length = this.Length;
    acc.Precision = this.Precision;
    acc.Mandatory = false;
    list.addAll(ObjectUtil.toList(db.toSQLArray(acc)));

    list.add(StringFormat.format("update ? set ?=?,?=null", new Object[] { this.Table, this.Column, this.Column + "_tmp", this.Column + "_tmp" }));

    dcc = new DropColumnCommand();
    dcc.Table = this.Table;
    dcc.Column = (this.Column + "_tmp");
    list.addAll(ObjectUtil.toList(db.toSQLArray(dcc)));

    String[] arr = new String[list.size()];
    return (String[])list.toArray(arr);
  }

  public void parse(String ddl) {
    ddl = ddl.substring("AdvanceChangeColumn:".length());
    JSONObject map = (JSONObject)JSON.parse(ddl);
    this.Table = map.getString("Table");
    this.Column = map.getString("Column");
    this.DataType = map.getInt("DataType");
    this.Length = map.getInt("Length");
    this.Precision = map.getInt("Precision");
    this.Mandatory = "true".equals(map.getString("Mandatory"));
    this.OldDataType = map.getInt("OldDataType");
    this.OldLength = map.getInt("OldLength");
    this.OldPrecision = map.getInt("OldPrecision");
    this.OldMandatory = "true".equals(map.getString("OldMandatory"));
  }

  public String toJSON() {
    Mapx map = new Mapx();
    map.put("Table", this.Table);
    map.put("Column", this.Column);
    map.put("DataType", Integer.valueOf(this.DataType));
    map.put("Length", Integer.valueOf(this.Length));
    map.put("Precision", Integer.valueOf(this.Precision));
    map.put("Mandatory", Boolean.valueOf(this.Mandatory));
    map.put("OldDataType", Integer.valueOf(this.OldDataType));
    map.put("OldLength", Integer.valueOf(this.OldLength));
    map.put("OldPrecision", Integer.valueOf(this.OldPrecision));
    map.put("OldMandatory", Boolean.valueOf(this.OldMandatory));
    return "AdvanceChangeColumn:" + JSON.toJSONString(map);
  }
}