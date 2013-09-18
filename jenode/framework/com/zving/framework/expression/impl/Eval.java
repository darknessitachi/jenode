package com.zving.framework.expression.impl;

import com.zving.framework.collection.Mapx;
import com.zving.framework.expression.core.ExpressionException;

public class Eval
{
  public static Object eval(String expression, Mapx<?, ?> variables)
  {
    CachedEvaluator ce = new CachedEvaluator();
    try {
      return ce.evaluate(expression, Object.class, new MapVariableResolver(variables), DefaultFunctionMapper.getInstance());
    } catch (ExpressionException e) {
      throw new RuntimeException(e);
    }
  }
}