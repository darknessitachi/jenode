package com.zving.framework.expression;

public class IntegerLiteral extends Literal
{
  public IntegerLiteral(String pToken)
  {
    super(getValueFromToken(pToken));
  }

  static Object getValueFromToken(String pToken)
  {
    return new Long(pToken);
  }

  public String getExpressionString()
  {
    return getValue().toString();
  }
}