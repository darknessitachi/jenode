package com.zving.framework.config;

import com.zving.framework.Config;

public class LoginMethod
  implements IApplicationConfigItem
{
  public static final String ID = "LoginMethod";

  public String getID()
  {
    return "LoginMethod";
  }

  public String getName() {
    return "Backend login method";
  }

  public static String getValue() {
    return Config.getValue("App.LoginMethod");
  }
}