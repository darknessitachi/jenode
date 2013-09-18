package com.zving.framework.core.scanner;

import com.zving.framework.extend.AbstractExtendService;

public class BuiltResourceVisitorService extends AbstractExtendService<IBuiltResourceVisitor>
{
  public static BuiltResourceVisitorService getInstance()
  {
    return (BuiltResourceVisitorService)findInstance(BuiltResourceVisitorService.class);
  }
}