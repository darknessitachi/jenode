package com.zving.platform.update;

import com.zving.framework.data.DataTable;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.NumberUtil;
import java.util.Date;

public class UpdateBL
{
  public static DataTable getUpdatePlugins(String server)
    throws Exception
  {
    UpdateServer us = new UpdateServer(server);
    us.loadContent();
    DataTable dt = new DataTable();
    dt.insertColumn("ID");
    dt.insertColumn("Time");
    dt.insertColumn("Size");
    dt.insertColumn("NeedUpdate");
    for (UpdateServer.PluginUpdateRecord upr : us.getPluginUpdateRecords()) {
      String time = DateUtil.toDateTimeString(new Date(upr.LastUpdateTime));
      String size = NumberUtil.format(Long.valueOf(upr.FileSize), "###,###,###");
      dt.insertRow(new Object[] { upr.ID, time, size, upr.NeedUpdate ? "Y" : "N" });
    }
    return dt;
  }

  public static void startUpdate()
  {
  }
}