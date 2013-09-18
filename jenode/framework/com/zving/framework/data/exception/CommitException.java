package com.zving.framework.data.exception;

public class CommitException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public CommitException(Exception e)
  {
    super(e);
  }
}