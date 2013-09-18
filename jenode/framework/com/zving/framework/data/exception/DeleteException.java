package com.zving.framework.data.exception;

public class DeleteException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public DeleteException(Exception e)
  {
    super(e);
  }

  public DeleteException(String message) {
    super(message);
  }
}