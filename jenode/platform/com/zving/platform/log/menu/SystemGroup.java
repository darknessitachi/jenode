package com.zving.platform.log.menu;

import com.zving.platform.ILogMenuGroup;

public class SystemGroup
  implements ILogMenuGroup
{
  public static final String ID = "System";

  public String getID()
  {
    return "System";
  }

  public String getName() {
    return "@{Logs.SystemLog}";
  }
}