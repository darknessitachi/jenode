package com.zving.framework.security.exception;

import com.zving.framework.core.FrameworkException;

public abstract class PrivException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public PrivException(String message)
  {
    super(message);
  }
}