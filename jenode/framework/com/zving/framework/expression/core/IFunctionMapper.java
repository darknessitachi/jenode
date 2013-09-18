package com.zving.framework.expression.core;

public abstract interface IFunctionMapper
{
  public abstract IFunction resolveFunction(String paramString1, String paramString2);

  public abstract void registerFunction(IFunction paramIFunction);
}