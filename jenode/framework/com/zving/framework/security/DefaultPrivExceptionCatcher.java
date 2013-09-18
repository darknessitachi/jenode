package com.zving.framework.security;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.ResponseData;
import com.zving.framework.config.MemberLoginPage;
import com.zving.framework.core.IExceptionCatcher;
import com.zving.framework.core.handler.AjaxHandler;
import com.zving.framework.security.exception.MemberNotLoginException;
import com.zving.framework.security.exception.PrivException;
import com.zving.framework.security.exception.UserNotLoginException;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.StringUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultPrivExceptionCatcher
  implements IExceptionCatcher
{
  public static final String ID = "com.zving.framework.security.DefaultPrivExceptionCatcher";

  public String getID()
  {
    return "com.zving.framework.security.DefaultPrivExceptionCatcher";
  }

  public String getName() {
    return "Default PrivException Catcher";
  }

  public Class<?>[] getTargetExceptionClass() {
    return new Class[] { PrivException.class };
  }

  public void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
    try {
      if ((e instanceof UserNotLoginException)) {
        String redirectURL = Config.getContextPath() + Config.getLoginPage();
        if ((Current.getURLHandler() instanceof AjaxHandler)) {
          ResponseData r = new ResponseData();
          r.put("_ZVING_SCRIPT", "window.location.href='" + redirectURL + "';");
          response.getWriter().write(r.toXML());
        } else {
          response.sendRedirect(redirectURL);
        }
      } else if ((e instanceof MemberNotLoginException)) {
        String redirectURL = Config.getContextPath() + MemberLoginPage.getValue();
        if (redirectURL.indexOf("?") > 0)
          redirectURL = redirectURL + "&url=";
        else {
          redirectURL = redirectURL + "?url=";
        }
        redirectURL = redirectURL + StringUtil.urlEncode(request.getRequestURI());
        if (ObjectUtil.notEmpty(request.getQueryString())) {
          redirectURL = redirectURL + StringUtil.urlEncode(new StringBuilder("&").append(request.getQueryString()).toString());
        }
        if ((Current.getURLHandler() instanceof AjaxHandler)) {
          ResponseData r = new ResponseData();
          r.put("_ZVING_SCRIPT", "window.location.href='" + redirectURL + "';");
          response.getWriter().write(r.toXML());
        } else {
          response.sendRedirect(redirectURL);
        }
      } else {
        String redirectURL = Config.getContextPath() + "framework/noPrivilege.zhtml?url=";
        if ((Current.getURLHandler() instanceof AjaxHandler)) {
          String method = request.getParameter("_ZVING_METHOD");
          if (ObjectUtil.notEmpty(method)) {
            redirectURL = redirectURL + "ajax!" + method;
          }
          if (ObjectUtil.notEmpty(request.getQueryString())) {
            redirectURL = redirectURL + "&" + request.getQueryString();
          }
          ResponseData r = new ResponseData();
          r.put("_ZVING_SCRIPT", "window.location.href='" + redirectURL + "';");
          response.getWriter().write(r.toXML());
        } else {
          redirectURL = redirectURL + request.getRequestURI();
          if (ObjectUtil.notEmpty(request.getQueryString())) {
            redirectURL = redirectURL + "&" + request.getQueryString();
          }
          response.sendRedirect(redirectURL);
        }
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
}