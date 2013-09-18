package com.zving.framework.data.exception;

public class CreateException extends DDLException
{
  private static final long serialVersionUID = 1L;

  public CreateException(Exception e)
  {
    super(e);
  }
}