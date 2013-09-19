package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class MailHost extends FixedConfigItem
{
  public static final String ID = "Platform.Mail.Host";

  public MailHost()
  {
    super("Platform.Mail.Host", "ShortText", "Text", "@{Platform.Mail.Host}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.Mail.Host");
    if (ObjectUtil.empty(v)) {
      v = null;
    }
    return v;
  }
}