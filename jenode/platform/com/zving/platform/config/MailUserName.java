package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class MailUserName extends FixedConfigItem
{
  public static final String ID = "Platform.Mail.UserName";

  public MailUserName()
  {
    super("Platform.Mail.UserName", "ShortText", "Text", "@{Platform.Mail.UserName}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.Mail.UserName");
    if (ObjectUtil.empty(v)) {
      v = null;
    }
    return v;
  }
}