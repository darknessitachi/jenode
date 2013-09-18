package com.zving.framework.orm;

import com.zving.framework.core.FrameworkException;

public class DAOException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public DAOException(String message)
  {
    super(message);
  }

  public DAOException(Throwable t) {
    super(t);
  }

  public DAOException(String message, Throwable cause) {
    super(message, cause);
  }
}