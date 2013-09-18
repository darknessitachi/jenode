package com.zving.framework.config;

import com.zving.framework.Config;

public class LogSQL
  implements IApplicationConfigItem
{
  public static final String ID = "Log.SQL";

  public String getID()
  {
    return "Log.SQL";
  }

  public String getName() {
    return "Show SQL log switch";
  }

  public static boolean getValue() {
    return !"false".equals(Config.getValue("App.Log.SQL"));
  }
}