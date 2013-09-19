package com.zving.platform.service;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.platform.ILogMenuGroup;

public class LogMenuGroupService extends AbstractExtendService<ILogMenuGroup>
{
  public static LogMenuGroupService getInstance()
  {
    return (LogMenuGroupService)findInstance(LogMenuGroupService.class);
  }
}