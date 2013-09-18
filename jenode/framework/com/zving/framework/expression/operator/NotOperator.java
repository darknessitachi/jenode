package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;

public class NotOperator extends UnaryOperator
{
  public static final NotOperator SINGLETON = new NotOperator();

  public String getOperatorSymbol()
  {
    return "not";
  }

  public Object apply(Object pValue, Logger pLogger)
    throws ExpressionException
  {
    boolean val = Coercions.coerceToBoolean(pValue, pLogger).booleanValue();

    return PrimitiveObjects.getBoolean(!val);
  }
}