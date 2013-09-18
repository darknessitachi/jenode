package com.zving.framework.security.exception;

public class MemberNotLoginException extends PrivException
{
  private static final long serialVersionUID = 1L;

  public MemberNotLoginException(String message)
  {
    super(message);
  }
}