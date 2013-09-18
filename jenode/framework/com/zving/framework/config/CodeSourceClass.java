package com.zving.framework.config;

import com.zving.framework.Config;

public class CodeSourceClass
  implements IApplicationConfigItem
{
  public static final String ID = "CodeSource";

  public String getID()
  {
    return "CodeSource";
  }

  public String getName() {
    return "CodeSource's subclass which provider code data";
  }

  public static String getValue() {
    return Config.getValue("App.CodeSource");
  }
}