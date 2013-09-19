package com.zving.platform.code;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.platform.FixedCodeType;
import com.zving.platform.pub.PlatformUtil;

public class SuccessOrFail extends FixedCodeType
{
  public static final String CODETYPE = "SuccessOrFail";
  public static final String SUCCESS = "S";
  public static final String FAIL = "F";

  public SuccessOrFail()
  {
    super("SuccessOrFail", "成功/失败", false, false);
    addFixedItem("S", "成功", null);
    addFixedItem("F", "失败", null);
  }

  public static boolean isSuccess(String str) {
    return "S".equals(str);
  }

  public static boolean isFail(String str) {
    return !isSuccess(str);
  }

  public static void decode(DataTable dt, String column) {
    String newColumnName = column + "Name";
    if (!dt.containsColumn(newColumnName)) {
      dt.insertColumn(newColumnName);
    }
    for (DataRow dr : dt) {
      String v = dr.getString(column);
      if (isSuccess(v))
        dr.set(newColumnName, "成功");
      else
        dr.set(newColumnName, "失败");
    }
  }

  public static String getName(String code)
  {
    return PlatformUtil.getCodeMap("SuccessOrFail").getString(code);
  }
}