package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class MenuOrder extends FixedConfigItem
{
  public static final String ID = "Platform.MenuOrder";

  public MenuOrder()
  {
    super("Platform.MenuOrder", "ShortText", "TextArea", "@{Platform.config.MenuOrder}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.MenuOrder");
    if (ObjectUtil.empty(v)) {
      v = null;
    }
    return v;
  }
}