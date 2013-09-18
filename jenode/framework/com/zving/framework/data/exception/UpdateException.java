package com.zving.framework.data.exception;

public class UpdateException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public UpdateException(Exception e)
  {
    super(e);
  }

  public UpdateException(String message) {
    super(message);
  }
}