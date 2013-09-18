package com.zving.framework.collection;

public abstract class Executor
{
  protected Object[] params;

  public Executor(Object[] params)
  {
    this.params = params;
  }

  public abstract boolean execute();
}