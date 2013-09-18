package com.zving.framework.expression.operator;

import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;
import com.zving.framework.utility.Operators;
import java.math.BigDecimal;
import java.math.BigInteger;

public class LessThanOrEqualsOperator extends RelationalOperator
{
  public static final LessThanOrEqualsOperator SINGLETON = new LessThanOrEqualsOperator();

  public String getOperatorSymbol()
  {
    return "<=";
  }

  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    return Operators.le(pLeft, pRight);
  }

  public boolean apply(double pLeft, double pRight)
  {
    return pLeft <= pRight;
  }

  public boolean apply(long pLeft, long pRight)
  {
    return pLeft <= pRight;
  }

  public boolean apply(String pLeft, String pRight)
  {
    return pLeft.compareTo(pRight) <= 0;
  }

  public boolean apply(BigDecimal pLeft, BigDecimal pRight)
  {
    return (isLess(pLeft.compareTo(pRight))) || (isEqual(pLeft.compareTo(pRight)));
  }

  public boolean apply(BigInteger pLeft, BigInteger pRight)
  {
    return (isLess(pLeft.compareTo(pRight))) || (isEqual(pLeft.compareTo(pRight)));
  }
}