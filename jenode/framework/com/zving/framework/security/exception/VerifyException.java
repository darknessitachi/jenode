package com.zving.framework.security.exception;

import com.zving.framework.core.FrameworkException;

public class VerifyException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public VerifyException(String message)
  {
    super(message);
  }
}