package com.zving.framework.expression.operator;

import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;

public abstract class UnaryOperator
{
  public abstract String getOperatorSymbol();

  public abstract Object apply(Object paramObject, Logger paramLogger)
    throws ExpressionException;
}