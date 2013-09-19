package com.zving.platform.extend;

import com.zving.framework.Config;
import com.zving.framework.extend.action.AfterAllPluginStartedAction;
import com.zving.platform.service.CodeService;

public class InitCodeAction extends AfterAllPluginStartedAction
{
  public void execute()
  {
    if (Config.isInstalled)
      CodeService.init();
  }

  public boolean isUsable() {
    return true;
  }
}