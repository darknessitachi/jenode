package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class DataServicePassword extends FixedConfigItem
{
  public static final String ID = "DataServicePassword";

  public DataServicePassword()
  {
    super("DataServicePassword", "ShortText", "Text", "@{Platform.Config.DataServicePassword}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("DataServicePassword");
    if (ObjectUtil.empty(v)) {
      v = "admin";
    }
    return v;
  }
}