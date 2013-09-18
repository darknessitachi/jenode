package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;

public abstract class EqualityOperator extends BinaryOperator
{
  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    return Coercions.applyEqualityOperator(pLeft, pRight, this, pLogger);
  }

  public abstract boolean apply(boolean paramBoolean, Logger paramLogger);
}