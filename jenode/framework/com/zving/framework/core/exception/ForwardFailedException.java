package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class ForwardFailedException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public ForwardFailedException(String message)
  {
    super(message);
  }

  public ForwardFailedException(Exception e) {
    super(e);
  }
}