package com.zving.platform.log.menu;

import com.zving.platform.ILogMenu;

public class SecurityLogMenu
  implements ILogMenu
{
  public String getID()
  {
    return "SecurityLog";
  }

  public String getName() {
    return "@{Logs.SecurityLogMenu}";
  }

  public String getDetailURL() {
    return "logs/securityLog.zhtml";
  }

  public String getGroupID() {
    return "System";
  }
}