package com.zving.platform.log.type;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataRow;
import com.zving.framework.data.DataTable;
import com.zving.platform.ILogType;

public class SQLLog
  implements ILogType
{
  public static final String ID = "SQLLog";
  public static final String SubType_Slow = "Slow";
  public static final String SubType_Error = "Error";
  public static final String SubType_Timeout = "Timeout";
  private Mapx<String, String> map;

  public String getID()
  {
    return "SQLLog";
  }

  public String getName() {
    return "@{Logs.SQLLogMenu}";
  }

  public Mapx<String, String> getSubTypes() {
    if (this.map == null) {
      this.map = new Mapx();
      this.map.put("Slow", "@{Platform.SlowSQL}");
      this.map.put("Error", "@{Platform.SQLError}");
      this.map.put("Timeout", "@{Platform.SQLTimeout}");
    }
    return this.map;
  }

  public void decodeMessage(DataTable dt) {
    dt.insertColumn("CostTime");
    for (DataRow dr : dt) {
      String message = dr.getString("LogMessage");
      String costTime = message.substring(message.indexOf("=") + 1, message.indexOf(","));
      message = message.substring(message.indexOf(",") + 1);
      dr.set("CostTime", costTime);
      dr.set("LogMessage", message);
    }
  }
}