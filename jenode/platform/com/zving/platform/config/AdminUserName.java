package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class AdminUserName extends FixedConfigItem
{
  public static final String ID = "Platform.AdminUserName";

  public AdminUserName()
  {
    super("Platform.AdminUserName", "ShortText", "Text", "@{Platform.config.AdminiUserName}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.AdminUserName");
    if (ObjectUtil.empty(v)) {
      v = "admin";
    }
    return v;
  }
}