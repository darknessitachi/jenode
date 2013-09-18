package com.zving.framework.core.handler;

import com.zving.framework.core.IURLHandler;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractTextHandler
  implements IURLHandler
{
  public boolean handle(String url, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    boolean flag = false;
    beforeExecute(url, request, response);
    flag = execute(url, request, response);
    if (flag) {
      afterExecute(url, request, response);
    }
    return flag;
  }

  public abstract boolean execute(String paramString, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, ServletException;

  public static void beforeExecute(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    response.setHeader("Pragma", "No-Cache");
    response.setHeader("Cache-Control", "No-Cache");
    response.setDateHeader("Expires", 0L);
    response.setContentType("text/html");
  }

  public static void afterExecute(String url, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
  }

  public void init()
  {
  }

  public void destroy()
  {
  }
}