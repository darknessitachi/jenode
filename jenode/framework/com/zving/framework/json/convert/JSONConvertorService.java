package com.zving.framework.json.convert;

import com.zving.framework.extend.AbstractExtendService;

public class JSONConvertorService extends AbstractExtendService<IJSONConvertor>
{
  public static JSONConvertorService getInstance()
  {
    return (JSONConvertorService)AbstractExtendService.findInstance(JSONConvertorService.class);
  }
}