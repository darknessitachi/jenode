package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Constants;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;
import java.math.BigDecimal;

public class DivideOperator extends BinaryOperator
{
  public static final DivideOperator SINGLETON = new DivideOperator();

  public String getOperatorSymbol()
  {
    return "/";
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

    if ((Coercions.isBigDecimal(pLeft)) || 
      (Coercions.isBigInteger(pLeft)) || 
      (Coercions.isBigDecimal(pRight)) || 
      (Coercions.isBigInteger(pRight)))
    {
      BigDecimal left = (BigDecimal)
        Coercions.coerceToPrimitiveNumber(pLeft, BigDecimal.class, pLogger);
      BigDecimal right = (BigDecimal)
        Coercions.coerceToPrimitiveNumber(pRight, BigDecimal.class, pLogger);
      try
      {
        return left.divide(right, 4);
      } catch (Exception exc) {
        if (pLogger.isLoggingError()) {
          pLogger.logError(
            Constants.ARITH_ERROR, 
            getOperatorSymbol(), 
            left, 
            right);
        }
        return PrimitiveObjects.getInteger(0);
      }
    }

    double left = 
      Coercions.coerceToPrimitiveNumber(pLeft, Double.class, pLogger)
      .doubleValue();
    double right = 
      Coercions.coerceToPrimitiveNumber(pRight, Double.class, pLogger)
      .doubleValue();
    try
    {
      return PrimitiveObjects.getDouble(left / right);
    } catch (Exception exc) {
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