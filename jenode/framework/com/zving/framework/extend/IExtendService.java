package com.zving.framework.extend;

import java.util.List;

public abstract interface IExtendService<T extends IExtendItem>
{
  public abstract void register(IExtendItem paramIExtendItem);

  public abstract T get(String paramString);

  public abstract T remove(String paramString);

  public abstract List<T> getAll();

  public abstract void destory();
}