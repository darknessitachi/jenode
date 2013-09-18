package com.zving.framework.data.exception;

public class SetAutoCommitException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public SetAutoCommitException(Exception e)
  {
    super(e);
  }
}