package com.zving.framework.data;

import com.zving.framework.collection.Mapx;
import com.zving.framework.json.JSON;
import com.zving.framework.json.JSONAware;
import java.io.Serializable;

public class DataColumn
  implements Serializable, Cloneable, JSONAware
{
  private static final long serialVersionUID = 1L;
  protected String ColumnName;
  protected int ColumnType;
  protected boolean isAllowNull = true;

  protected String dateFormat = null;

  public DataColumn()
  {
  }

  public Object clone() {
    return new DataColumn(this.ColumnName, this.ColumnType);
  }

  public DataColumn(String columnName, int columnType) {
    this.ColumnName = columnName;
    this.ColumnType = columnType;
  }

  public DataColumn(String columnName, int columnType, boolean allowNull) {
    this.ColumnName = columnName;
    this.ColumnType = columnType;
    this.isAllowNull = allowNull;
  }

  public String getColumnName() {
    return this.ColumnName;
  }

  public DataColumn setColumnName(String columnName) {
    this.ColumnName = columnName;
    return this;
  }

  public int getColumnType() {
    return this.ColumnType;
  }

  public DataColumn setColumnType(int columnType) {
    this.ColumnType = columnType;
    return this;
  }

  public boolean isAllowNull() {
    return this.isAllowNull;
  }

  public DataColumn setAllowNull(boolean isAllowNull) {
    this.isAllowNull = isAllowNull;
    return this;
  }

  public String getDateFormat() {
    return this.dateFormat;
  }

  public DataColumn setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
    return this;
  }

  public String toJSONString() {
    Mapx mapx = new Mapx();
    mapx.put("Name", this.ColumnName);
    mapx.put("Type", Integer.valueOf(this.ColumnType));
    return JSON.toJSONString(mapx);
  }
}