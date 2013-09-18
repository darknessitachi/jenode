package com.zving.framework;

import com.zving.framework.core.Dispatcher;
import com.zving.framework.utility.ObjectUtil;

public abstract class UIFacade
{
  protected ResponseData Response;
  protected RequestData Request;
  protected CookieData Cookies;

  public UIFacade()
  {
    this.Request = Current.getRequest();
    this.Cookies = Current.getCookies();
    this.Response = Current.getResponse();
    if (this.Response == null) {
      this.Response = new ResponseData();
    }
    if (this.Cookies == null) {
      this.Cookies = new CookieData();
    }
    if (this.Request == null)
      this.Request = new RequestData();
  }

  public String $V(String id)
  {
    String v = this.Request.getString(id);
    if (ObjectUtil.empty(v)) {
      return this.Response.getString(id);
    }
    return v;
  }

  public UIFacade $S(String id, Object value)
  {
    this.Response.put(id, value);
    return this;
  }

  public ResponseData getResponse()
  {
    return this.Response;
  }

  public CookieData getCookies()
  {
    return this.Cookies;
  }

  public void redirect(String url) {
    Dispatcher.redirect(url);
  }

  public void success(String message) {
    this.Response.setSuccessMessage(message);
  }

  public void fail(String message) {
    this.Response.setFailedMessage(message);
  }
}