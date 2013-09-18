package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class BeanGetPropertyException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public BeanGetPropertyException(String message)
  {
    super(message);
  }

  public BeanGetPropertyException(Exception e) {
    super(e);
  }
}