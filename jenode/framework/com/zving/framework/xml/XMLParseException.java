package com.zving.framework.xml;

import com.zving.framework.core.FrameworkException;

public class XMLParseException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public XMLParseException(String message)
  {
    super(message);
  }

  public XMLParseException(Throwable t) {
    super(t);
  }

  public XMLParseException(String message, Throwable cause) {
    super(message, cause);
  }
}