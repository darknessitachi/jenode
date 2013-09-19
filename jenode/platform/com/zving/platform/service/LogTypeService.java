package com.zving.platform.service;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.platform.ILogType;

public class LogTypeService extends AbstractExtendService<ILogType>
{
  public static LogTypeService getInstance()
  {
    return (LogTypeService)findInstance(LogTypeService.class);
  }
}