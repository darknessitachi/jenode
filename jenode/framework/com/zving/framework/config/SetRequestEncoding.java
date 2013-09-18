package com.zving.framework.config;

import com.zving.framework.Config;

public class SetRequestEncoding
  implements IApplicationConfigItem
{
  public static final String ID = "SetRequestEncoding";

  public String getID()
  {
    return "SetRequestEncoding";
  }

  public String getName() {
    return "Set request encoding switch";
  }

  public static boolean getValue() {
    return !"false".equals(Config.getValue("App.SetRequestEncoding"));
  }
}