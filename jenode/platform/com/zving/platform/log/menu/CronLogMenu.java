package com.zving.platform.log.menu;

import com.zving.platform.ILogMenu;

public class CronLogMenu
  implements ILogMenu
{
  public String getID()
  {
    return "CronLog";
  }

  public String getName() {
    return "@{Logs.CronLogMenu}";
  }

  public String getDetailURL() {
    return "logs/cronLog.zhtml";
  }

  public String getGroupID() {
    return "System";
  }
}