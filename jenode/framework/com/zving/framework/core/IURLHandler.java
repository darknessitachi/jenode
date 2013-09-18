package com.zving.framework.core;

import com.zving.framework.extend.IExtendItem;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface IURLHandler extends IExtendItem
{
  public abstract boolean match(String paramString);

  public abstract boolean handle(String paramString, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, ServletException;

  public abstract void init();

  public abstract int getOrder();

  public abstract void destroy();
}