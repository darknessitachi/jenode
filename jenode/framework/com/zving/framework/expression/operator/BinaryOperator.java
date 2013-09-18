package com.zving.framework.expression.operator;

import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;

public abstract class BinaryOperator
{
  public abstract String getOperatorSymbol();

  public abstract Object apply(Object paramObject1, Object paramObject2, Logger paramLogger)
    throws ExpressionException;

  public boolean shouldEvaluate(Object pLeft)
  {
    return true;
  }

  public boolean shouldCoerceToBoolean()
  {
    return false;
  }
}