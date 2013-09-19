package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class MailPassword extends FixedConfigItem
{
  public static final String ID = "Platform.Mail.Password";

  public MailPassword()
  {
    super("Platform.Mail.Password", "ShortText", "Text", "@{Platform.Mail.Password}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.Mail.Password");
    if (ObjectUtil.empty(v)) {
      v = null;
    }
    return v;
  }
}