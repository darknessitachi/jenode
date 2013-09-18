package com.zving.framework.config;

import com.zving.framework.Config;

public class SetResponseEncoding
  implements IApplicationConfigItem
{
  public static final String ID = "SetResponseEncoding";

  public String getID()
  {
    return "SetResponseEncoding";
  }

  public String getName() {
    return "Set response encoding switch";
  }

  public static boolean getValue() {
    return !"false".equals(Config.getValue("App.SetResponseEncoding"));
  }
}