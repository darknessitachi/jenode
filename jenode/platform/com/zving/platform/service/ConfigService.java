package com.zving.platform.service;

import com.zving.framework.extend.AbstractExtendService;
import com.zving.platform.FixedConfigItem;

public class ConfigService extends AbstractExtendService<FixedConfigItem>
{
  public static ConfigService getInstance()
  {
    return (ConfigService)findInstance(ConfigService.class);
  }
}