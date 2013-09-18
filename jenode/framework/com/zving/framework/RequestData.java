package com.zving.framework;

import com.zving.framework.collection.CaseIgnoreMapx;
import com.zving.framework.collection.Mapx;
import com.zving.framework.data.DataCollection;
import javax.servlet.http.HttpServletRequest;

public class RequestData extends DataCollection
{
  private static final long serialVersionUID = 1L;
  private Mapx<String, Object> Headers = new CaseIgnoreMapx();
  private HttpServletRequest request;
  private CookieData Cookies = null;
  private String URL;
  private String QueryString;
  private String ClientIP;
  private String ClassName;
  private String ServerName;
  private int Port;
  private String Scheme;

  public String getServerName()
  {
    return this.ServerName;
  }

  public void setServerName(String serverName)
  {
    this.ServerName = serverName;
  }

  public int getPort()
  {
    return this.Port;
  }

  public void setPort(int port)
  {
    this.Port = port;
  }

  public String getScheme()
  {
    return this.Scheme;
  }

  public void setScheme(String scheme)
  {
    this.Scheme = scheme;
  }

  public String getClassName()
  {
    return this.ClassName;
  }

  public void setClassName(String className)
  {
    this.ClassName = className;
  }

  public String getClientIP()
  {
    return this.ClientIP;
  }

  public void setClientIP(String clientIP)
  {
    this.ClientIP = clientIP;
  }

  public String getURL()
  {
    return this.URL;
  }

  public void setURL(String url)
  {
    this.URL = url;
  }

  public Mapx<String, Object> getHeaders() {
    return this.Headers;
  }

  public void setHeaders(Mapx<String, Object> headers) {
    this.Headers = headers;
  }

  public String getQueryString() {
    return this.QueryString;
  }

  public void setQueryString(String queryString) {
    this.QueryString = queryString;
  }

  public CookieData getCookies() {
    return this.Cookies;
  }

  public void setCookies(CookieData cookies) {
    this.Cookies = cookies;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletRequest getRequest() {
    return this.request;
  }
}