package com.zving.framework.expression.operator;

import com.zving.framework.expression.Coercions;
import com.zving.framework.expression.Logger;
import com.zving.framework.expression.core.ExpressionException;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class ArithmeticOperator extends BinaryOperator
{
  public Object apply(Object pLeft, Object pRight, Logger pLogger)
    throws ExpressionException
  {
    return Coercions.applyArithmeticOperator(pLeft, pRight, this, pLogger);
  }

  public abstract double apply(double paramDouble1, double paramDouble2);

  public abstract long apply(long paramLong1, long paramLong2);

  public abstract BigDecimal apply(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2);

  public abstract BigInteger apply(BigInteger paramBigInteger1, BigInteger paramBigInteger2);
}