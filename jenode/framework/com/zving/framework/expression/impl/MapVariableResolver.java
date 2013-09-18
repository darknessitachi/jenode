package com.zving.framework.expression.impl;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IVariableResolver;
import java.util.Map;

public class MapVariableResolver
  implements IVariableResolver
{
  private Map<?, ?> map;

  public MapVariableResolver(Map<?, ?> map)
  {
    this.map = map;
  }

  public Object resolveVariable(String varName) throws ExpressionException {
    return this.map.get(varName);
  }

  public void setVariable(String varName, Object value)
  {
  }
}