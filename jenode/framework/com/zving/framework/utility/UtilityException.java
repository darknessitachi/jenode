package com.zving.framework.utility;

import com.zving.framework.core.FrameworkException;

public class UtilityException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public UtilityException(String message)
  {
    super(message);
  }

  public UtilityException(Throwable t) {
    super(t);
  }

  public UtilityException(String message, Throwable cause) {
    super(message, cause);
  }
}