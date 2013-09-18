package com.zving.framework.json;

import com.zving.framework.core.FrameworkException;

public class JSONException extends FrameworkException
{
  private static final long serialVersionUID = 1L;

  public JSONException(String message)
  {
    super(message);
  }

  public JSONException(Throwable t) {
    super(t);
  }

  public JSONException(String message, Throwable cause) {
    super(message, cause);
  }
}