package com.zving.framework.config;

import com.zving.framework.Config;

public class ExcludeClassScan
  implements IApplicationConfigItem
{
  public static final String ID = "ExcludeClassScan";

  public String getID()
  {
    return "ExcludeClassScan";
  }

  public String getName() {
    return "Class or package exclude in annotation scanning";
  }

  public static String getValue() {
    return Config.getValue("App.ExcludeClassScan");
  }
}