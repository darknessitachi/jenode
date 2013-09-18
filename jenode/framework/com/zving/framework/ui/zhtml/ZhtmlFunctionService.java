package com.zving.framework.ui.zhtml;

import com.zving.framework.expression.core.IFunction;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.impl.DefaultFunctionMapper;
import com.zving.framework.extend.AbstractExtendService;

public class ZhtmlFunctionService extends AbstractExtendService<IFunction>
{
  private static ZhtmlFunctionService instance;
  private static IFunctionMapper mapper;

  public static ZhtmlFunctionService getInstance()
  {
    if (instance == null) {
      synchronized (ZhtmlFunctionService.class) {
        if (instance == null) {
          instance = (ZhtmlFunctionService)findInstance(ZhtmlFunctionService.class);
        }
      }
    }
    return instance;
  }

  public static IFunctionMapper getFunctionMappper() {
    if (mapper == null) {
      synchronized (ZhtmlFunctionService.class) {
        if (mapper == null) {
          mapper = new DefaultFunctionMapper();
          for (IFunction f : getInstance().getAll()) {
            mapper.registerFunction(f);
          }
        }
      }
    }
    return mapper;
  }
}