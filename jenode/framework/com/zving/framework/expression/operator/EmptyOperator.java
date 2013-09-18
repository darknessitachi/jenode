package com.zving.framework.expression.operator;

import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class EmptyOperator extends UnaryOperator
{
  public static final EmptyOperator SINGLETON = new EmptyOperator();

  public String getOperatorSymbol()
  {
    return "empty";
  }

  public Object apply(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    if (pValue == null) {
      return PrimitiveObjects.getBoolean(true);
    }

    if ("".equals(pValue)) {
      return PrimitiveObjects.getBoolean(true);
    }

    if ((pValue.getClass().isArray()) && (Array.getLength(pValue) == 0)) {
      return PrimitiveObjects.getBoolean(true);
    }

    if (((pValue instanceof Map)) && (((Map)pValue).isEmpty())) {
      return PrimitiveObjects.getBoolean(true);
    }

    if (((pValue instanceof Collection)) && (((Collection)pValue).isEmpty())) {
      return PrimitiveObjects.getBoolean(true);
    }

    return PrimitiveObjects.getBoolean(false);
  }
}