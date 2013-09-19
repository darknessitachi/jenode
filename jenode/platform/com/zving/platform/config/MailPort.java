package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class MailPort extends FixedConfigItem
{
  public static final String ID = "Platform.Mail.Port";

  public MailPort()
  {
    super("Platform.Mail.Port", "ShortText", "Text", "@{Platform.Mail.Port}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.Mail.Port");
    if (ObjectUtil.empty(v)) {
      v = "25";
    }
    return v;
  }
}