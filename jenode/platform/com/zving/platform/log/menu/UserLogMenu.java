package com.zving.platform.log.menu;

import com.zving.platform.ILogMenu;

public class UserLogMenu
  implements ILogMenu
{
  public String getID()
  {
    return "UserLog";
  }

  public String getName() {
    return "@{Logs.UserLogMenu}";
  }

  public String getDetailURL() {
    return "logs/userLog.zhtml";
  }

  public String getGroupID() {
    return "System";
  }
}