package com.zving.framework.xml;

import com.zving.framework.core.FrameworkException;

public class XMLWriteException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public XMLWriteException(String message)
  {
    super(message);
  }

  public XMLWriteException(Throwable t) {
    super(t);
  }

  public XMLWriteException(String message, Throwable cause) {
    super(message, cause);
  }
}