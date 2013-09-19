package com.zving.platform.log.type;

import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataTable;
import com.zving.platform.ILogType;

public class CronLog
  implements ILogType
{
  public static final String ID = "CronLog";
  public static final String SubType_Start = "Start";
  public static final String SubType_Blocked = "Blocked";
  public static final String SubType_End = "End";
  private Mapx<String, String> map;

  public String getID()
  {
    return "CronLog";
  }

  public String getName() {
    return "@{Logs.CronLogMenu}";
  }

  public Mapx<String, String> getSubTypes() {
    if (this.map == null) {
      this.map = new Mapx();
      this.map.put("Start", "@{Platform.TaskStart}");
    }
    return this.map;
  }

  public void decodeMessage(DataTable dt)
  {
  }
}