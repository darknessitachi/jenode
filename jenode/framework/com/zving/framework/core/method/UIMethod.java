package com.zving.framework.core.method;

public abstract class UIMethod
{
  public void init()
  {
  }

  public abstract void execute();

  public boolean validate()
  {
    return true;
  }
}