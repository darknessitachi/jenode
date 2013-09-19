package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class AppDataPath extends FixedConfigItem
{
  public static final String ID = "Platform.AppDataPath";

  public AppDataPath()
  {
    super("Platform.AppDataPath", "ShortText", "Text", "@{Platform.config.AppDataPath}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.AppDataPath");
    if (ObjectUtil.empty(v))
      v = Config.getContextRealPath() + "WEB-INF/data/";
    else {
      v = FixedConfigItem.replacePathHolder(v);
    }
    if (!v.endsWith("/")) {
      v = v + "/";
    }
    return v;
  }
}