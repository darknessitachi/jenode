package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class BeanInitException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public BeanInitException(String message)
  {
    super(message);
  }

  public BeanInitException(Exception e) {
    super(e);
  }
}