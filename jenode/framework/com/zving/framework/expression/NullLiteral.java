package com.zving.framework.expression;

public class NullLiteral extends Literal
{
  public static final NullLiteral SINGLETON = new NullLiteral();

  public NullLiteral()
  {
    super(null);
  }

  public String getExpressionString()
  {
    return "null";
  }
}