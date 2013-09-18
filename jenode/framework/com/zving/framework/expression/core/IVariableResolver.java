package com.zving.framework.expression.core;

public abstract interface IVariableResolver
{
  public abstract Object resolveVariable(String paramString)
    throws ExpressionException;
}