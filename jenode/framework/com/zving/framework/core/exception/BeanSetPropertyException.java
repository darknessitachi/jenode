package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class BeanSetPropertyException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public BeanSetPropertyException(String message)
  {
    super(message);
  }

  public BeanSetPropertyException(Exception e) {
    super(e);
  }
}