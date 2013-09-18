package com.zving.framework.core.exception;

public class UIMethodInvokeException extends UIMethodException
{
  private static final long serialVersionUID = 1L;

  public UIMethodInvokeException(String message)
  {
    super(message);
  }

  public UIMethodInvokeException(Exception e) {
    super(e);
  }
}