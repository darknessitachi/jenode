package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.expression.core.IFunctionMapper;
import com.zving.framework.expression.core.IVariableResolver;

public abstract class Expression
{
  public abstract String getExpressionString();

  public abstract Object evaluate(IVariableResolver paramIVariableResolver, IFunctionMapper paramIFunctionMapper, Logger paramLogger)
    throws ExpressionException;
}