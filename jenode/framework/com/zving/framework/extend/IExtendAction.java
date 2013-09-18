package com.zving.framework.extend;

public abstract interface IExtendAction
{
  public abstract Object execute(Object[] paramArrayOfObject)
    throws ExtendException;

  public abstract boolean isUsable();
}