package com.zving.framework.expression;

public class FloatingPointLiteral extends Literal
{
  public FloatingPointLiteral(String pToken)
  {
    super(getValueFromToken(pToken));
  }

  static Object getValueFromToken(String pToken)
  {
    return new Double(pToken);
  }

  public String getExpressionString()
  {
    return getValue().toString();
  }
}