package com.zving.framework.data.exception;

import com.zving.framework.core.FrameworkException;

public class DatabaseException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public DatabaseException(Exception e)
  {
    super(e);
  }

  public DatabaseException(String message) {
    super(message);
  }
}