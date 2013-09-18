package com.zving.framework.expression;

import com.zving.framework.expression.core.ExpressionException;
import java.io.PrintStream;
import java.text.MessageFormat;

public class Logger
{
  PrintStream mOut;

  public Logger(PrintStream pOut)
  {
    this.mOut = pOut;
  }

  public boolean isLoggingWarning()
  {
    return false;
  }

  public void logWarning(String pMessage, Throwable pRootCause)
    throws ExpressionException
  {
    if (isLoggingWarning())
      if (pMessage == null) {
        System.out.println(pRootCause);
      }
      else if (pRootCause == null) {
        System.out.println(pMessage);
      }
      else
        System.out.println(pMessage + ": " + pRootCause);
  }

  public void logWarning(String pTemplate)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(pTemplate, null);
  }

  public void logWarning(Throwable pRootCause)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(null, pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0 }), 
        pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0, Object pArg1)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1 }), 
        pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0, Object pArg1, Object pArg2)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2 }), 
        pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3 }), 
        pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4 }), 
        pRootCause);
  }

  public void logWarning(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4, Object pArg5)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4, 
        pArg5 }));
  }

  public void logWarning(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4, Object pArg5)
    throws ExpressionException
  {
    if (isLoggingWarning())
      logWarning(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4, 
        pArg5 }), 
        pRootCause);
  }

  public boolean isLoggingError()
  {
    return true;
  }

  public void logError(String pMessage, Throwable pRootCause)
    throws ExpressionException
  {
    if (isLoggingError()) {
      if (pMessage == null) {
        throw new ExpressionException(pRootCause);
      }
      if (pRootCause == null) {
        throw new ExpressionException(pMessage);
      }

      throw new ExpressionException(pMessage, pRootCause);
    }
  }

  public void logError(String pTemplate)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(pTemplate, null);
  }

  public void logError(Throwable pRootCause)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(null, pRootCause);
  }

  public void logError(String pTemplate, Object pArg0)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0 }), 
        pRootCause);
  }

  public void logError(String pTemplate, Object pArg0, Object pArg1)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1 }), 
        pRootCause);
  }

  public void logError(String pTemplate, Object pArg0, Object pArg1, Object pArg2)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2 }), 
        pRootCause);
  }

  public void logError(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3 }), 
        pRootCause);
  }

  public void logError(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4 }), 
        pRootCause);
  }

  public void logError(String pTemplate, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4, Object pArg5)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4, 
        pArg5 }));
  }

  public void logError(String pTemplate, Throwable pRootCause, Object pArg0, Object pArg1, Object pArg2, Object pArg3, Object pArg4, Object pArg5)
    throws ExpressionException
  {
    if (isLoggingError())
      logError(
        MessageFormat.format(
        pTemplate, 
        new Object[] { 
        pArg0, 
        pArg1, 
        pArg2, 
        pArg3, 
        pArg4, 
        pArg5 }), 
        pRootCause);
  }
}