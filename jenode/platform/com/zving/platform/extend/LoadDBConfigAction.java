package com.zving.platform.extend;

import com.zving.framework.extend.action.AfterConfigInitAction;
import com.zving.platform.pub.PlatformUtil;

public class LoadDBConfigAction extends AfterConfigInitAction
{
  public Object execute(Object[] args)
  {
    PlatformUtil.loadDBConfig();
    return null;
  }

  public boolean isUsable() {
    return true;
  }
}