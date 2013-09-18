package com.zving.framework.expression.core;

public abstract interface IExpression
{
  public abstract Object evaluate(IVariableResolver paramIVariableResolver)
    throws ExpressionException;
}