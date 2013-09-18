package com.zving.framework.extend.exception;

import com.zving.framework.core.FrameworkException;

public class CreateExtendItemInstanceException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public CreateExtendItemInstanceException(String message)
  {
    super(message);
  }

  public CreateExtendItemInstanceException(Throwable t) {
    super(t);
  }

  public CreateExtendItemInstanceException(String message, Throwable cause) {
    super(message, cause);
  }
}