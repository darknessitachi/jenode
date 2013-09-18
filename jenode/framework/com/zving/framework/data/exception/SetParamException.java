package com.zving.framework.data.exception;

public class SetParamException extends DatabaseException
{
  private static final long serialVersionUID = 1L;

  public SetParamException(Exception e)
  {
    super(e);
  }
}