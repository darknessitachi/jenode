package com.zving.framework.security.exception;

public class NoPrivException extends PrivException
{
  private static final long serialVersionUID = 1L;

  public NoPrivException(String message)
  {
    super(message);
  }
}