package com.zving.framework.config;

import com.zving.framework.Config;

public class MemberLoginPage
  implements IApplicationConfigItem
{
  public static final String ID = "MemberLoginPage";

  public String getID()
  {
    return "MemberLoginPage";
  }

  public String getName() {
    return "Member login URL";
  }

  public static String getValue() {
    return Config.getValue("App.MemberLoginPage");
  }
}