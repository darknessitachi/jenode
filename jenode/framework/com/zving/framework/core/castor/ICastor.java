package com.zving.framework.core.castor;

import com.zving.framework.extend.IExtendItem;

public abstract interface ICastor extends IExtendItem
{
  public abstract boolean canCast(Class<?> paramClass);

  public abstract Object cast(Object paramObject, Class<?> paramClass);
}