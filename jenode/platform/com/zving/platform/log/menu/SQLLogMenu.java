package com.zving.platform.log.menu;

import com.zving.platform.ILogMenu;

public class SQLLogMenu
  implements ILogMenu
{
  public String getID()
  {
    return "SQLLog";
  }

  public String getName() {
    return "@{Logs.SQLLogMenu}";
  }

  public String getDetailURL() {
    return "logs/SQLLog.zhtml";
  }

  public String getGroupID() {
    return "System";
  }
}