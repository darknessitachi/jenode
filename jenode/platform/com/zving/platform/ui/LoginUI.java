package com.zving.platform.ui;

import com.zving.framework.Config;
import com.zving.framework.Current;
import com.zving.framework.RequestData;
import com.zving.framework.ResponseData;
import com.zving.framework.UIFacade;
import com.zving.framework.User;
import com.zving.framework.annotation.Alias;
import com.zving.framework.annotation.Priv;
import com.zving.framework.data.Q;
import com.zving.framework.extend.ExtendManager;
import com.zving.framework.i18n.Lang;
import com.zving.framework.orm.DAOSet;
import com.zving.framework.security.LicenseInfo;
import com.zving.framework.security.PasswordUtil;
import com.zving.framework.utility.DateUtil;
import com.zving.framework.utility.LogUtil;
import com.zving.framework.utility.ObjectUtil;
import com.zving.framework.utility.ServletUtil;
import com.zving.framework.utility.StringFormat;
import com.zving.framework.utility.StringUtil;
import com.zving.platform.bl.UserBL;
import com.zving.platform.code.Enable;
import com.zving.platform.config.AdminUserName;
import com.zving.schema.ZDUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Alias("Login")
public class LoginUI extends UIFacade
{
  private static ArrayList<String> wrongList = new ArrayList();

  @Priv(login=false)
  public void init() {
    String title = Lang.get("Product.Name");
    $S("Title", title == null ? Config.getAppName() : title);
    if (LicenseUI.needWarning()) {
      String warnStr = Lang.get("Login.LicenseWarning");
      $S("LicenseWarning", new StringFormat(warnStr, new Object[] { DateUtil.toString(LicenseInfo.getEndDate()) }) + "<br>");
    } else {
      $S("LicenseWarning", "");
    }
    if (LicenseUI.nearEndDateWarning()) {
      String warnStr = Lang.get("Login.LinceseNearEndWarning");

      $S("LinceseNearEndWarning", new StringFormat(warnStr, new Object[] { DateUtil.toString(LicenseInfo.getEndDate()) }));
    } else {
      $S("LinceseNearEndWarning", "");
    }
    if (ObjectUtil.notIn(new Object[] { User.getLanguage(), "en", "zh-cn" })) {
      if (User.getLanguage().equals("zh-tw"))
        $S("Language", "zh-cn");
      else
        $S("Language", "en");
    }
    else
      $S("Language", User.getLanguage());
  }

  public static void ssoLogin(HttpServletRequest request, HttpServletResponse response, UIFacade ui)
  {
    String username = ui.$V("u");
    if (username == null) {
      return;
    }
    if (Config.isFrontDeploy()) {
      return;
    }
    ZDUser user = new ZDUser();
    user.setUserName(username);
    DAOSet userSet = user.query();
    if ((!Config.isAllowLogin) && (!username.equalsIgnoreCase(AdminUserName.getValue()))) {
      return;
    }
    if ((userSet != null) && (userSet.size() >= 1))
    {
      user = (ZDUser)userSet.get(0);
      String ip = ServletUtil.getRealIP(request);
      Current.getRequest().setClientIP(ip);
      UserBL.login(user);
      ExtendManager.invoke("com.zving.platform.AfterSSOLogin", new Object[] { ui });
      try {
        String path = request.getParameter("Referer");
        LogUtil.info("SSOLogin,Referer:" + path);
        if ("JSONP".equalsIgnoreCase(request.getParameter("j"))) {
          String serverUrl = request.getParameter("serverUrl");
          if (StringUtil.isNotEmpty(path)) {
            if ((StringUtil.isNotEmpty(request.getParameter("t"))) && (!"null".equalsIgnoreCase(request.getParameter("t"))))
              response.getWriter().write(StringUtil.concat(new Object[] { "window.location.href = '", serverUrl, path, "?t=", request.getParameter("t"), "';" }));
            else
              response.getWriter().write(StringUtil.concat(new Object[] { "window.location.href = '", serverUrl, path, "';" }));
          }
          else {
            response.getWriter().write(StringUtil.concat(new Object[] { "window.location.href = '", serverUrl, "application.zhtml';" }));
          }
        }
        else if (StringUtil.isNotEmpty(path)) {
          if ((StringUtil.isNotEmpty(request.getParameter("t"))) && (!"null".equalsIgnoreCase(request.getParameter("t"))))
            response.sendRedirect(path + "?t=" + request.getParameter("t"));
          else
            response.sendRedirect(path);
        }
        else {
          response.sendRedirect("application.zhtml");
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Priv(login=false)
  public void submit() {
    if (Config.isFrontDeploy()) {
      return;
    }
    String userName = $V("UserName").toLowerCase();
    if (wrongList.contains(userName)) {
      Object authCode = User.getValue("_ZVING_AUTHKEY");
      if ((authCode == null) || (!authCode.toString().equalsIgnoreCase($V("VerifyCode")))) {
        fail(Lang.get("Common.InvalidVerifyCode"));
        return;
      }
    }
    ZDUser user = new ZDUser();
    user.setUserName(userName);
    DAOSet userSet = user.query(new Q(" where UserName=?", new Object[] { userName }));

    if ((!Config.isAllowLogin) && (!user.getUserName().equalsIgnoreCase(AdminUserName.getValue()))) {
      fail(Lang.get("User.DenyLoginTemp"));
      return;
    }

    if ((userSet == null) || (userSet.size() < 1)) {
      fail(Lang.get("Common.UserNameOrPasswordWrong"));
    }
    else {
      user = (ZDUser)userSet.get(0);
      if (!PasswordUtil.verify($V("Password"), user.getPassword())) {
        fail(Lang.get("Common.UserNameOrPasswordWrong"));
        if (!wrongList.contains(userName)) {
          synchronized (wrongList) {
            if (wrongList.size() > 20000) {
              wrongList.clear();
            }
            wrongList.add(userName);
          }
        }
        return;
      }
      if ((!AdminUserName.getValue().equalsIgnoreCase(user.getUserName())) && (Enable.isDisable(user.getStatus()))) {
        fail(Lang.get("User.UserStopped"));
        return;
      }
      UserBL.login(user);
      this.Response.setStatus(1);
      synchronized (wrongList) {
        wrongList.remove(userName);
      }
      $S("_ZVING_SCRIPT", "window.location=\"application.zhtml\";");
    }
  }
}