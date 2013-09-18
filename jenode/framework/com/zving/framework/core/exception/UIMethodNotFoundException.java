package com.zving.framework.core.exception;

public class UIMethodNotFoundException extends UIMethodException
{
  private static final long serialVersionUID = 1L;

  public UIMethodNotFoundException(String message)
  {
    super(message);
  }

  public UIMethodNotFoundException(Exception e) {
    super(e);
  }
}