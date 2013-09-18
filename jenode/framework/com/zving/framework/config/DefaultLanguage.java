package com.zving.framework.config;

import com.zving.framework.Config;

public class DefaultLanguage
  implements IApplicationConfigItem
{
  public static final String ID = "DefaultLanguage";

  public String getID()
  {
    return "DefaultLanguage";
  }

  public String getName() {
    return "Backend default language";
  }

  public static String getValue() {
    return Config.getValue("App.DefaultLanguage");
  }
}