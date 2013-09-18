package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Constants;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class UnaryMinusOperator extends UnaryOperator
{
  public static final UnaryMinusOperator SINGLETON = new UnaryMinusOperator();

  public String getOperatorSymbol()
  {
    return "-";
  }

  public Object apply(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null)
    {
      return PrimitiveObjects.getInteger(0);
    }

    if ((pValue instanceof BigInteger)) {
      return ((BigInteger)pValue).negate();
    }

    if ((pValue instanceof BigDecimal)) {
      return ((BigDecimal)pValue).negate();
    }

    if ((pValue instanceof String)) {
      if (Coercions.isFloatingPointString(pValue)) {
        double dval = 
          Coercions.coerceToPrimitiveNumber(
          pValue, Double.class, pLogger)
          .doubleValue();
        return PrimitiveObjects.getDouble(-dval);
      }

      long lval = 
        Coercions.coerceToPrimitiveNumber(
        pValue, Long.class, pLogger)
        .longValue();
      return PrimitiveObjects.getLong(-lval);
    }

    if ((pValue instanceof Byte)) {
      return PrimitiveObjects.getByte(
        (byte)-((Byte)pValue).byteValue());
    }
    if ((pValue instanceof Short)) {
      return PrimitiveObjects.getShort(
        (short)-((Short)pValue).shortValue());
    }
    if ((pValue instanceof Integer)) {
      return PrimitiveObjects.getInteger(
        -((Integer)pValue).intValue());
    }
    if ((pValue instanceof Long)) {
      return PrimitiveObjects.getLong(
        -((Long)pValue).longValue());
    }
    if ((pValue instanceof Float)) {
      return PrimitiveObjects.getFloat(
        -((Float)pValue).floatValue());
    }
    if ((pValue instanceof Double)) {
      return PrimitiveObjects.getDouble(
        -((Double)pValue).doubleValue());
    }

    if (pLogger.isLoggingError()) {
      pLogger.logError(
        Constants.UNARY_OP_BAD_TYPE, 
        getOperatorSymbol(), 
        pValue.getClass().getName());
    }
    return PrimitiveObjects.getInteger(0);
  }
}