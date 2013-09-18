package com.zving.framework.expression.core;

public class ExpressionException extends Exception
{
  private static final long serialVersionUID = 1L;
  private Throwable mRootCause;

  public ExpressionException()
  {
  }

  public ExpressionException(String pMessage)
  {
    super(pMessage);
  }

  public ExpressionException(Throwable pRootCause) {
    super(pRootCause.getLocalizedMessage());
    this.mRootCause = pRootCause;
  }

  public ExpressionException(String pMessage, Throwable pRootCause) {
    super(pMessage);
    this.mRootCause = pRootCause;
  }

  public Throwable getRootCause() {
    return this.mRootCause;
  }
}