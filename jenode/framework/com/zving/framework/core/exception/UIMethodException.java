package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class UIMethodException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public UIMethodException(String message)
  {
    super(message);
  }

  public UIMethodException(Exception e) {
    super(e);
  }
}