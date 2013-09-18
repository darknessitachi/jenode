package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class RelationalOperator extends BinaryOperator
{
  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    return Coercions.applyRelationalOperator(pLeft, pRight, this, pLogger);
  }

  public abstract boolean apply(double paramDouble1, double paramDouble2);

  public abstract boolean apply(long paramLong1, long paramLong2);

  public abstract boolean apply(String paramString1, String paramString2);

  public abstract boolean apply(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2);

  public abstract boolean apply(BigInteger paramBigInteger1, BigInteger paramBigInteger2);

  protected boolean isLess(int val)
  {
    if (val < 0) {
      return true;
    }
    return false;
  }

  protected boolean isEqual(int val)
  {
    if (val == 0) {
      return true;
    }
    return false;
  }

  protected boolean isGreater(int val)
  {
    if (val > 0) {
      return true;
    }
    return false;
  }
}