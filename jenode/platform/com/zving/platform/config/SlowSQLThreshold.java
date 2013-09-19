package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class SlowSQLThreshold extends FixedConfigItem
{
  public static final String ID = "Platform.SlowSQLThreshold";

  public SlowSQLThreshold()
  {
    super("Platform.SlowSQLThreshold", "Long", "Text", "@{Platform.config.SlowSQLThreshold}", "com.zving.framework");
  }

  public static int getValue() {
    String v = Config.getValue("Platform.SlowSQLThreshold");
    if (ObjectUtil.empty(v)) {
      return 1000;
    }
    return Integer.parseInt(v);
  }
}