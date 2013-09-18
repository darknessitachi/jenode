package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Constants;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;
import java.math.BigInteger;

public class ModulusOperator extends BinaryOperator
{
  public static final ModulusOperator SINGLETON = new ModulusOperator();

  public String getOperatorSymbol()
  {
    return "%";
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

    if (((pLeft != null) && (
      (Coercions.isFloatingPointType(pLeft)) || 
      (Coercions.isFloatingPointString(pLeft)))) || 
      (Coercions.isBigDecimal(pLeft)) || (
      (pRight != null) && (
      (Coercions.isFloatingPointType(pRight)) || 
      (Coercions.isFloatingPointString(pRight)) || 
      (Coercions.isBigDecimal(pRight))))) {
      double left = 
        Coercions.coerceToPrimitiveNumber(pLeft, Double.class, pLogger)
        .doubleValue();
      double right = 
        Coercions.coerceToPrimitiveNumber(pRight, Double.class, pLogger)
        .doubleValue();
      try
      {
        return PrimitiveObjects.getDouble(left % right);
      }
      catch (Exception exc) {
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
    if ((Coercions.isBigInteger(pLeft)) || (Coercions.isBigInteger(pRight))) {
      BigInteger left = (BigInteger)
        Coercions.coerceToPrimitiveNumber(pLeft, BigInteger.class, pLogger);
      BigInteger right = (BigInteger)
        Coercions.coerceToPrimitiveNumber(pRight, BigInteger.class, pLogger);
      try
      {
        return left.remainder(right);
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

    long left = 
      Coercions.coerceToPrimitiveNumber(pLeft, Long.class, pLogger)
      .longValue();
    long right = 
      Coercions.coerceToPrimitiveNumber(pRight, Long.class, pLogger)
      .longValue();
    try
    {
      return PrimitiveObjects.getLong(left % right);
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