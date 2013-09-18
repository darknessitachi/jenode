package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.PrimitiveObjects;
import com.zving.framework.expression.core.ExpressionException;

public class OrOperator extends BinaryOperator
{
  public static final OrOperator SINGLETON = new OrOperator();

  public String getOperatorSymbol()
  {
    return "or";
  }

  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    boolean left = 
      Coercions.coerceToBoolean(pLeft, pLogger).booleanValue();
    boolean right = 
      Coercions.coerceToBoolean(pRight, pLogger).booleanValue();

    return PrimitiveObjects.getBoolean((left) || (right));
  }

  public boolean shouldEvaluate(Object pLeft)
  {
    return ((pLeft instanceof Boolean)) && 
      (!((Boolean)pLeft).booleanValue());
  }

  public boolean shouldCoerceToBoolean()
  {
    return true;
  }
}