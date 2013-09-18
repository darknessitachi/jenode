package com.zving.framework.core;

import com.zving.framework.Current;

public class Dispatcher
{
  protected String forwardURL;
  protected String redirectURL;

  public static void forward(String url)
  {
    Dispatcher d = Current.getDispatcher();
    if (d == null) {
      return;
    }
    d.forwardURL = url;
    d.redirectURL = null;
    throw new DispatchException();
  }

  public static void redirect(String url) {
    Dispatcher d = Current.getDispatcher();
    if (d == null) {
      return;
    }
    d.forwardURL = null;
    d.redirectURL = url;
    throw new DispatchException();
  }

  public String getForwardURL()
  {
    return this.forwardURL;
  }

  public String getRedirectURL() {
    return this.redirectURL;
  }

  public static class DispatchException extends Error
  {
    private static final long serialVersionUID = 1L;
  }
}