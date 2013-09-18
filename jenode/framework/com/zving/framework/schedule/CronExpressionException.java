package com.zving.framework.schedule;

public class CronExpressionException extends Exception
{
  private static final long serialVersionUID = 1L;

  public CronExpressionException(String message)
  {
    super(message);
  }
}