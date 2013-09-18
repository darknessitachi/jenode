package com.zving.framework.core.exception;

public class UIMethodCreateException extends UIMethodException
{
  private static final long serialVersionUID = 1L;

  public UIMethodCreateException(String message)
  {
    super(message);
  }

  public UIMethodCreateException(Exception e) {
    super(e);
  }
}