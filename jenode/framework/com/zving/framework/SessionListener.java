package com.zving.framework;

import com.zving.framework.extend.ExtendManager;
import com.zving.framework.utility.FileUtil;
import com.zving.preloader.facade.HttpSessionListenerFacade;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener
  implements HttpSessionListener
{
  public void sessionCreated(HttpSessionEvent arg0)
  {
    Config.OnlineUserCount += 1;
    ExtendManager.invoke("com.zving.framework.AfterSessionCreate", new Object[] { arg0.getSession() });
  }

  public void sessionDestroyed(HttpSessionEvent arg0)
  {
    Config.OnlineUserCount -= 1;
    User.UserData u = getUserDataFromSession(arg0.getSession());
    if (u != null) {
      if ((u.isLogin()) && 
        (Config.LoginUserCount > 0)) {
        Config.LoginUserCount -= 1;
      }

      if (Config.isDebugMode()) {
        FileUtil.delete(Config.getContextRealPath() + "WEB-INF/cache/" + u.getSessionID());
      }
    }
    ExtendManager.invoke("com.zving.framework.BeforeSessionDestory", new Object[] { arg0.getSession() });
  }

  public static void forceExit()
  {
    Map map = HttpSessionListenerFacade.getMap();
    for (Object k : map.keySet().toArray())
      if (!k.equals(User.getSessionID()))
      {
        ((HttpSession)map.get(k)).invalidate();
      }
  }

  public static void forceExit(String sid)
  {
    HttpSession session = (HttpSession)HttpSessionListenerFacade.getMap().get(sid);
    session.invalidate();
  }

  public static User.UserData[] getUsers()
  {
    ArrayList arr = new ArrayList();
    for (HttpSession session : HttpSessionListenerFacade.getMap().values()) {
      User.UserData u = getUserDataFromSession(session);
      if (u != null) {
        arr.add(u);
      }
    }
    return (User.UserData[])arr.toArray(new User.UserData[arr.size()]);
  }

  public static User.UserData[] getUsers(String status)
  {
    ArrayList arr = new ArrayList();
    for (HttpSession session : HttpSessionListenerFacade.getMap().values()) {
      User.UserData u = getUserDataFromSession(session);
      if ((u != null) && 
        (status.equalsIgnoreCase(u.getStatus()))) {
        arr.add(u);
      }
    }

    return (User.UserData[])arr.toArray(new User.UserData[arr.size()]);
  }

  public static ArrayList<String> getUserNames(String status)
  {
    User.UserData[] arr = getUsers(status);
    ArrayList userNameArr = new ArrayList(arr.length);
    for (int i = 0; i < arr.length; i++) {
      userNameArr.add(arr[i].getUserName());
    }
    return userNameArr;
  }

  public static User.UserData getUser(String userName)
  {
    User.UserData[] users = getUsers();
    for (int i = 0; i < users.length; i++) {
      if (userName.equals(users[i].getUserName())) {
        return users[i];
      }
    }
    return null;
  }

  public static User.UserData getUserDataFromSession(HttpSession session)
  {
    Object o = session.getAttribute("_ZVING_USER");
    if (o != null) {
      if (!(o instanceof User.UserData)) {
        return null;
      }
      return (User.UserData)o;
    }
    return null;
  }
}