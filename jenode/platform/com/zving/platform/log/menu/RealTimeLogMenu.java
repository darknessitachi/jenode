package com.zving.platform.log.menu;

import com.zving.platform.ILogMenu;

public class RealTimeLogMenu
  implements ILogMenu
{
  public String getID()
  {
    return "RealTimeLog";
  }

  public String getName() {
    return "@{Logs.ConsoleLog}";
  }

  public String getDetailURL() {
    return "logs/realTime.zhtml";
  }

  public String getGroupID() {
    return "System";
  }
}