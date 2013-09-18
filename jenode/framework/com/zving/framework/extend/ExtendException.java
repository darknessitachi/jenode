package com.zving.framework.extend;

public class ExtendException extends Exception
{
  private static final long serialVersionUID = 1L;
  private String message;

  public ExtendException(String message)
  {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }
}