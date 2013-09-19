package com.zving.platform;

import com.zving.framework.extend.plugin.FrameworkPlugin;
import com.zving.platform.pub.PlatformUtil;

public class PlatformPlugin extends FrameworkPlugin
{
  public void start()
  {
    PlatformUtil.loadDBConfig();
  }
}