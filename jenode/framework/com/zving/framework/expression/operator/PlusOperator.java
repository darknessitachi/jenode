package com.zving.framework.expression.operator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PlusOperator extends ArithmeticOperator
{
  public static final PlusOperator SINGLETON = new PlusOperator();

  public String getOperatorSymbol()
  {
    return "+";
  }

  public double apply(double pLeft, double pRight)
  {
    return pLeft + pRight;
  }

  public long apply(long pLeft, long pRight)
  {
    return pLeft + pRight;
  }

  public BigDecimal apply(BigDecimal pLeft, BigDecimal pRight)
  {
    return pLeft.add(pRight);
  }

  public BigInteger apply(BigInteger pLeft, BigInteger pRight)
  {
    return pLeft.add(pRight);
  }
}