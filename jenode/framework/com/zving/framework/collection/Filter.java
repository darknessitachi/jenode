package com.zving.framework.collection;

public abstract class Filter<T>
{
  protected Object Param;

  public Filter()
  {
  }

  public Filter(Object param)
  {
    this.Param = param;
  }

  public abstract boolean filter(T paramT);
}