package com.zving.framework.core.handler;

import com.zving.framework.Config;
import com.zving.framework.CookieData;
import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.core.IURLHandler;
import com.zving.framework.core.exception.UIMethodNotFoundException;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.data.BlockingTransaction;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.utility.LogUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionHandler
  implements IURLHandler
{
  public static final String ID = "com.zving.framework.core.ActionHandler";

  public String getID()
  {
    return "com.zving.framework.core.ActionHandler";
  }

  public String getName() {
    return "ZAction URL Processor";
  }

  public boolean match(String url) {
    int i = url.indexOf("?");
    if (i > 0) {
      url = url.substring(0, i);
    }
    if (url.endsWith(".zaction")) {
      return true;
    }
    if (url.endsWith("/")) {
      return false;
    }
    i = url.lastIndexOf("/");
    if (i > 0) {
      url = url.substring(i + 1);
    }
    if (url.indexOf(".") < 0) {
      return true;
    }
    return false;
  }

  public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      if (url.endsWith(".zaction")) {
        url = url.substring(0, url.length() - 8);
      }
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);
      try
      {
        IMethodLocator method = MethodLocatorUtil.find(url.substring(1));
        if (method == null) {
          return false;
        }
        return invoke(request, response, method);
      } catch (UIMethodNotFoundException e) {
        return false;
      }
    } finally {
      BlockingTransaction.clearTransactionBinding();
    }
  }

  public static boolean invoke(HttpServletRequest request, HttpServletResponse response, IMethodLocator method) throws ServletException, IOException
  {
    PrivCheck.check(method);

    if (!VerifyCheck.check(method)) {
      String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
      LogUtil.warn(message);
      Current.getResponse().setFailedMessage(message);
      response.getWriter().write(Current.getResponse().toXML());
      return true;
    }
    ZAction action = new ZAction();
    CookieData cookies = new CookieData(request);
    action.setCookies(cookies);
    action.setRequest(request);
    action.setResponse(response);
    try {
      response.setContentType("text/html");
      method.execute(new Object[] { action });

      if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
        response.setContentType("text/html;charset=" + Config.getGlobalCharset());
      else {
        response.setCharacterEncoding(Config.getGlobalCharset());
      }

      if (!action.isBinaryMode())
        try {
          response.getWriter().print(action.getHTML());
        } catch (Exception localException) {
        }
    }
    finally {
      if (action.isBinaryMode()) {
        response.getOutputStream().close();
      }
    }
    return true;
  }

  public void init() {
  }

  public void destroy() {
  }

  public int getOrder() {
    return 9999;
  }
}