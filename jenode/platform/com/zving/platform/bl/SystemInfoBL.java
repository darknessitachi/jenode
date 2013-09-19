package com.zving.platform.bl;

import com.zving.framework.extend.plugin.PluginConfig;
import com.zving.framework.extend.plugin.PluginManager;

public class SystemInfoBL
{
  private static String appVersion;

  public static String getAppVersion()
  {
    if (appVersion == null) {
      appVersion = "Unknown";
      PluginConfig pc = PluginManager.getInstance().getPluginConfig("com.zving.product");
      if (pc == null) {
        pc = PluginManager.getInstance().getPluginConfig("com.zving.framework");
      }
      if (pc != null) {
        appVersion = pc.getVersion();
      }
    }
    return appVersion;
  }
}