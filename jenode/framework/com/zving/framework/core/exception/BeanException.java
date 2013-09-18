package com.zving.framework.core.exception;

import com.zving.framework.core.FrameworkException;

public class BeanException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public BeanException(String message)
  {
    super(message);
  }

  public BeanException(Exception e) {
    super(e);
  }
}