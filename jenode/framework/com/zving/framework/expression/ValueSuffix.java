package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public abstract class ValueSuffix
{
  public abstract String getExpressionString();

  public abstract Object evaluate(Object paramObject, IVariableResolver paramIVariableResolver, IFunctionMapper paramIFunctionMapper, Logger paramLogger)
    throws ExpressionException;
}