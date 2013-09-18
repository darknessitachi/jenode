package com.zving.framework.expression.core;

import com.zving.framework.extend.IExtendItem;

public abstract interface IFunction extends IExtendItem
{
  public abstract String getFunctionPrefix();

  public abstract String getFunctionName();

  public abstract Class<?>[] getArgumentTypes();

  public abstract Object execute(IVariableResolver paramIVariableResolver, Object[] paramArrayOfObject)
    throws ExpressionException;
}