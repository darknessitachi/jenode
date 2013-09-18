package com.zving.framework.security.exception;

public class UserNotLoginException extends PrivException
{
  private static final long serialVersionUID = 1L;

  public UserNotLoginException(String message)
  {
    super(message);
  }
}