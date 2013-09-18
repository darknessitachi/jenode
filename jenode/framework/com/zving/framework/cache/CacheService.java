package com.zving.framework.cache;

import com.zving.framework.extend.AbstractExtendService;

public class CacheService extends AbstractExtendService<CacheDataProvider>
{
  public static CacheService getInstance()
  {
    return (CacheService)findInstance(CacheService.class);
  }
}