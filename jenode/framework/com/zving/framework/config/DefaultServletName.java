package com.zving.framework.config;

import com.zving.framework.Config;

public class DefaultServletName
  implements IApplicationConfigItem
{
  public static final String ID = "DefaultServletName";

  public String getID()
  {
    return "DefaultServletName";
  }

  public String getName() {
    return "Default servlet's name in current container";
  }

  public static String getValue() {
    return Config.getValue("App.DefaultServletName");
  }
}