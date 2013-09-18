package com.zving.framework.extend.action;

import com.zving.framework.RequestData;
import java.util.ArrayList;

public class ZhtmlContext
{
  StringBuilder sb = new StringBuilder();
  ArrayList<String> includes = new ArrayList();
  RequestData request = null;

  public ZhtmlContext(RequestData request) {
    this.request = request;
  }

  public RequestData getRequest() {
    return this.request;
  }

  protected String getOut() {
    return this.sb.toString();
  }

  protected ArrayList<String> getIncludes() {
    return this.includes;
  }

  public void write(Object obj)
  {
    this.sb.append(obj);
  }

  public void include(String file)
  {
    this.includes.add(file);
  }
}