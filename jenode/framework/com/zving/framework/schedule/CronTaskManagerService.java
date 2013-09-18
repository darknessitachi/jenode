package com.zving.framework.schedule;

import com.zving.framework.extend.AbstractExtendService;

public class CronTaskManagerService extends AbstractExtendService<AbstractTaskManager>
{
  public static CronTaskManagerService getInstance()
  {
    return (CronTaskManagerService)findInstance(CronTaskManagerService.class);
  }
}