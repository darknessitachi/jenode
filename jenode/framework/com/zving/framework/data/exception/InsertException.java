package com.zving.framework.data.exception;

public class InsertException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public InsertException(Exception e)
  {
    super(e);
  }

  public InsertException(String message) {
    super(message);
  }
}