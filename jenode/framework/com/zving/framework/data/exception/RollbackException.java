package com.zving.framework.data.exception;

public class RollbackException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public RollbackException(Exception e)
  {
    super(e);
  }
}