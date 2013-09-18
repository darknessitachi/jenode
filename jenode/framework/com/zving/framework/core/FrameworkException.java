package com.zving.framework.core;

public class FrameworkException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public FrameworkException(String message)
  {
    super(message);
  }

  public FrameworkException(Throwable t) {
    super(t);
  }

  public FrameworkException(String message, Throwable cause) {
    super(message, cause);
  }
}