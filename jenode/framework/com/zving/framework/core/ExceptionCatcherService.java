package com.zving.framework.core;

import com.zving.framework.extend.AbstractExtendService;

public class ExceptionCatcherService extends AbstractExtendService<IExceptionCatcher>
{
  public static ExceptionCatcherService getInstance()
  {
    return (ExceptionCatcherService)AbstractExtendService.findInstance(ExceptionCatcherService.class);
  }
}