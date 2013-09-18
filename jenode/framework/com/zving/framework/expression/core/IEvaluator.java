package com.zving.framework.expression.core;

public abstract interface IEvaluator
{
  public abstract Object evaluate(String paramString, Class<?> paramClass, IVariableResolver paramIVariableResolver, IFunctionMapper paramIFunctionMapper)
    throws ExpressionException;
}