package com.zving.framework.extend.exception;

import com.zving.framework.core.FrameworkException;

public class CreateExtendActionInstanceException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public CreateExtendActionInstanceException(String message)
  {
    super(message);
  }

  public CreateExtendActionInstanceException(Throwable t) {
    super(t);
  }

  public CreateExtendActionInstanceException(String message, Throwable cause) {
    super(message, cause);
  }
}