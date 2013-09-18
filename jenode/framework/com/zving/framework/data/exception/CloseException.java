package com.zving.framework.data.exception;

public class CloseException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public CloseException(Exception e)
  {
    super(e);
  }
}