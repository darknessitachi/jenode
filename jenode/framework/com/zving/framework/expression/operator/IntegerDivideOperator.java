package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Constants;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;

public class IntegerDivideOperator extends BinaryOperator
{
  public static final IntegerDivideOperator SINGLETON = new IntegerDivideOperator();

  public String getOperatorSymbol()
  {
    return "idiv";
  }

  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    if ((pLeft == null) && 
      (pRight == null)) {
      if (pLogger.isLoggingWarning()) {
        pLogger.logWarning(
          Constants.ARITH_OP_NULL, 
          getOperatorSymbol());
      }
      return PrimitiveObjects.getInteger(0);
    }

    long left = 
      Coercions.coerceToPrimitiveNumber(pLeft, Long.class, pLogger)
      .longValue();
    long right = 
      Coercions.coerceToPrimitiveNumber(pRight, Long.class, pLogger)
      .longValue();
    try
    {
      return PrimitiveObjects.getLong(left / right);
    }
    catch (Exception exc) {
      if (pLogger.isLoggingError())
        pLogger.logError(
          Constants.ARITH_ERROR, 
          getOperatorSymbol(), 
          left, 
          right);
    }
    return PrimitiveObjects.getInteger(0);
  }
}