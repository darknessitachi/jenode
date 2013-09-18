package com.zving.framework.expression.operator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MultiplyOperator extends ArithmeticOperator
{
  public static final MultiplyOperator SINGLETON = new MultiplyOperator();

  public String getOperatorSymbol()
  {
    return "*";
  }

  public double apply(double pLeft, double pRight)
  {
    return pLeft * pRight;
  }

  public long apply(long pLeft, long pRight)
  {
    return pLeft * pRight;
  }

  public BigDecimal apply(BigDecimal pLeft, BigDecimal pRight)
  {
    return pLeft.multiply(pRight);
  }

  public BigInteger apply(BigInteger pLeft, BigInteger pRight)
  {
    return pLeft.multiply(pRight);
  }
}