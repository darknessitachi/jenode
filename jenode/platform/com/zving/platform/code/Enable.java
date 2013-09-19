package com.zving.platform.code;

import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.platform.FixedCodeType;

public class Enable extends FixedCodeType
{
  public static final String Enable = "Y";
  public static final String Disable = "N";

  public Enable()
  {
    super("Enable", "是否启用", false, false);
    addFixedItem("Y", "启用", null);
    addFixedItem("N", "停用", null);
  }

  public static boolean isEnable(String str) {
    return "Y".equals(str);
  }

  public static boolean isDisable(String str) {
    return "N".equals(str);
  }

  public static void decode(DataTable dt, String column) {
    String newColumnName = column + "Name";
    if (!dt.containsColumn(newColumnName)) {
      dt.insertColumn(newColumnName);
    }
    for (DataRow dr : dt) {
      String v = dr.getString(column);
      if (isEnable(v))
        dr.set(newColumnName, "启用");
      else if (isDisable(v))
        dr.set(newColumnName, "停用");
    }
  }
}