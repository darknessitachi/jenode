package com.zving.framework.json.convert;

import com.zving.framework.core.FrameworkException;

public class JSONConvertException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public JSONConvertException(String message)
  {
    super(message);
  }

  public JSONConvertException(Throwable t) {
    super(t);
  }

  public JSONConvertException(String message, Throwable cause) {
    super(message, cause);
  }
}