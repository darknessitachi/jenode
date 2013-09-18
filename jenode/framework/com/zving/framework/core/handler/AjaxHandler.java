package com.zving.framework.core.handler;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.config.LoginMethod;
import com.zving.framework.core.Dispatcher;
import com.zving.framework.core.Dispatcher.DispatchException;
import com.zving.framework.core.exception.UIMethodNotFoundException;
import com.zving.framework.core.method.IMethodLocator;
import com.zving.framework.core.method.MethodLocatorUtil;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.security.PrivCheck;
import com.zving.framework.security.VerifyCheck;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.StringUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AjaxHandler extends AbstractTextHandler
{
  public static final String ID = "com.zving.framework.core.AjaxHandler";

  public String getID()
  {
    return "com.zving.framework.core.AjaxHandler";
  }

  public boolean match(String url) {
    return url.startsWith("/ajax/invoke");
  }

  public String getName() {
    return "Ajax Server Invoke Processor";
  }

  public boolean execute(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      response.setContentType("text/xml");

      if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
        response.setContentType("text/xml;charset=utf-8");
      else {
        response.setCharacterEncoding("UTF-8");
      }
      request.setCharacterEncoding("UTF-8");

      String method = request.getParameter("_ZVING_METHOD");
      String data = request.getParameter("_ZVING_DATA");
      String resultDataFormat = request.getParameter("_ZVING_DATA_FORMAT");
      if (resultDataFormat == null) {
        if (data.startsWith("{"))
          resultDataFormat = "json";
        else if (data.startsWith("<")) {
          resultDataFormat = "xml";
        }
      }

      if ("/index.zhtml".equals(request.getServletPath())) {
        response.sendRedirect("login.zhtml");
        return true;
      }

      String loginMethods = LoginMethod.getValue();
      IMethodLocator m = MethodLocatorUtil.find(method);
      if (m == null) {
        throw new UIMethodNotFoundException(method);
      }
      if (StringUtil.isNotEmpty(loginMethods)) {
        String checkMethod = m.getName();
        loginMethods = "," + loginMethods + ",";
        if (loginMethods.indexOf("," + checkMethod + ",") >= 0) {
          request.getSession().invalidate();
          Cookie[] cs = request.getCookies();
          for (int i = 0; (cs != null) && (i < cs.length); i++) {
            if (cs[i] != null) {
              cs[i].setMaxAge(0);
            }
          }
        }
      }
      if (!"true".equals(request.getParameter("_ZVING_NOSESSION"))) {
        request.getSession(true);
      }

      if (("".equals(url)) || ("/".equals(url))) {
        url = "/index.zhtml";
      }

      if (StringUtil.isEmpty(method)) {
        LogUtil.warn("Error in Server.sendRequest(),QueryString=" + request.getQueryString() + ",Referer=" + 
          request.getHeader("referer"));
        return true;
      }
      PrivCheck.check(m);

      if (!VerifyCheck.check(m)) {
        String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
        LogUtil.warn(message);
        Current.getResponse().setFailedMessage(message);
        response.getWriter().write(Current.getResponse().toXML());
        return true;
      }

      ExtendManager.invoke("com.zving.framework.BeforeUIMethodInvoke", new Object[] { method });

      m.execute(new Object[0]);

      ExtendManager.invoke("com.zving.framework.AfterUIMethodInvoke", new Object[] { method });
      String result;
      if ((StringUtil.isNotEmpty(resultDataFormat)) && (resultDataFormat.equalsIgnoreCase("json"))) {
        String result = Current.getResponse().toJSON();
        response.setContentType("application/json");
      } else {
        result = Current.getResponse().toXML();
      }

      response.getWriter().write(result);
    } catch (Dispatcher.DispatchException e) {
      ResponseData r = new ResponseData();
      String redirectURL = Current.getDispatcher().getRedirectURL();
      if (redirectURL == null) {
        redirectURL = Current.getDispatcher().getForwardURL();
      }
      r.put("_ZVING_SCRIPT", "window.location.href='" + Config.getContextPath() + redirectURL + "';");
      response.getWriter().write(r.toXML());
    }
    return true;
  }

  public void init() {
  }

  public void destroy() {
  }

  public int getOrder() {
    return 9998;
  }
}