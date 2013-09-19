package com.zving.platform.config;

import com.zving.framework.Config;
import com.zving.framework.utility.ObjectUtil;
import com.zving.platform.FixedConfigItem;

public class UpdateServerURLs extends FixedConfigItem
{
  public static final String ID = "Platform.UpdateServerURLs";
  private static final String NormalUpdateServer = "http://release.update.zving.com/";
  private static final String BetaUpdateServer = "http://beta.update.zving.com/";

  public UpdateServerURLs()
  {
    super("Platform.UpdateServerURLs", "LargeText", "TextArea", "@{Platform.config.UpdateServerURLs}", "com.zving.framework");
  }

  public static String getValue() {
    String v = Config.getValue("Platform.UpdateServerURLs");
    if (ObjectUtil.empty(v)) {
      v = "http://release.update.zving.com/\nhttp://beta.update.zving.com/";
    } else {
      if (v.indexOf("http://release.update.zving.com/") < 0) {
        v = v + "\nhttp://release.update.zving.com/";
      }
      if (v.indexOf("http://beta.update.zving.com/") < 0) {
        v = v + "\nhttp://beta.update.zving.com/";
      }
    }
    return v;
  }
}