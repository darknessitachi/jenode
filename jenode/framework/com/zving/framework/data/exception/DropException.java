package com.zving.framework.data.exception;

public class DropException extends DDLException
{
  private static final long serialVersionUID = 1L;

  public DropException(Exception e)
  {
    super(e);
  }
}