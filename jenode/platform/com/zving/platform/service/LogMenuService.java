package com.zving.platform.service;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.platform.ILogMenu;

public class LogMenuService extends AbstractExtendService<ILogMenu>
{
  public static LogMenuService getInstance()
  {
    return (LogMenuService)findInstance(LogMenuService.class);
  }
}