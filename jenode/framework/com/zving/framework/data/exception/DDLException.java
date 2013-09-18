package com.zving.framework.data.exception;

public abstract class DDLException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public DDLException(Exception e)
  {
    super(e);
  }
}