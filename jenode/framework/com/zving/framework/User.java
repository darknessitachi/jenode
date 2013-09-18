package com.zving.framework;

import com.zving.framework.collection.Mapx;
import com.zving.framework.core.FrameworkException;
import com.zving.framework.i18n.LangMapping;
import com.zving.framework.security.Privilege;
import com.zving.framework.utility.LogUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class User
{
  public static String getUserName()
  {
    return getCurrent().UserName;
  }

  public static void setUserName(String UserName)
  {
    getCurrent().setUserName(UserName);
  }

  public static String getRealName()
  {
    return getCurrent().RealName;
  }

  public static void setRealName(String RealName)
  {
    getCurrent().RealName = RealName;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static String getBranchInnerCode()
  {
    return getCurrent().BranchInnerCode;
  }

  public static void setBranchInnerCode(String BranchInnerCode)
  {
    getCurrent().BranchInnerCode = BranchInnerCode;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static boolean isBranchAdministrator()
  {
    return getCurrent().BranchAdminFlag;
  }

  public static void setBranchAdministrator(boolean flag)
  {
    getCurrent().BranchAdminFlag = flag;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static String getType()
  {
    return getCurrent().Type;
  }

  public static void setType(String Type)
  {
    getCurrent().Type = Type;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static String getStatus()
  {
    return getCurrent().Status;
  }

  public static void setStatus(String Status)
  {
    getCurrent().Status = Status;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static int getValueCount()
  {
    return getCurrent().size();
  }

  public static Object getValue(Object key)
  {
    return getCurrent().get(key);
  }

  public static Mapx<String, Object> getValues()
  {
    return getCurrent();
  }

  public static void setValue(String key, Object value)
  {
    Mapx map = getCurrent();
    synchronized (map) {
      map.put(key, value);
    }
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static boolean isLogin()
  {
    return getCurrent().isLogin;
  }

  public static void setLogin(boolean isLogin)
  {
    if (isLogin) {
      if (Config.isFrontDeploy()) {
        throw new FrameworkException("User cann't login in Front Deploy mode!");
      }
      if (!getCurrent().isLogin) {
        Config.LoginUserCount += 1;
      }

    }
    else if ((getCurrent().isLogin) && 
      (Config.LoginUserCount > 0)) {
      Config.LoginUserCount -= 1;
    }

    getCurrent().isLogin = isLogin;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static void setCurrent(UserData user)
  {
    Current.setUser(user);
    if (Config.isDebugMode())
      cacheUser(user);
  }

  public static UserData getCurrent()
  {
    UserData ud = Current.getUser();
    if (ud == null) {
      ud = new UserData();
      setCurrent(ud);
    }
    return ud;
  }

  protected static void cacheUser(UserData u)
  {
    if ((getCurrent() != u) || (getCurrent() == null)) {
      return;
    }
    if (Config.isDebugMode())
      try {
        File dir = new File(Config.getContextRealPath() + "WEB-INF/cache/");
        if (!dir.exists()) {
          dir.mkdirs();
        }
        if (u.getSessionID() == null) {
          return;
        }
        FileOutputStream f = new FileOutputStream(Config.getContextRealPath() + "WEB-INF/cache/" + u.getSessionID());
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(u);
        s.flush();
        s.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  public static UserData getCachedUser(String sessionID)
  {
    try
    {
      File in = new File(Config.getContextRealPath() + "WEB-INF/cache/" + sessionID);
      if (in.exists()) {
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(in));
        Object o = s.readObject();
        if (UserData.class.isInstance(o)) {
          s.close();
          in.delete();
          UserData u = (UserData)o;
          if (u.isLogin()) {
            Config.LoginUserCount += 1;
          }
          return u;
        }
        s.close();
      }
    } catch (Exception e) {
      LogUtil.warn("getCachedUser() failed");
    }
    return null;
  }

  public static void destory()
  {
    setCurrent(new UserData());
  }

  public static String getSessionID()
  {
    return getCurrent().SessionID;
  }

  protected static void setSessionID(String sessionID)
  {
    getCurrent().SessionID = sessionID;
    if (Config.isDebugMode())
      cacheUser(getCurrent());
  }

  public static String getLanguage()
  {
    return getCurrent().Language;
  }

  public static void setLanguage(String language)
  {
    getCurrent().Language = language;
  }

  public static Privilege getPrivilege()
  {
    return getCurrent().Priv;
  }

  public static void setPrivilege(Privilege priv)
  {
    getCurrent().Priv = priv;
  }

  public static Member.MemberData getMemberData() {
    return getCurrent().MemberData;
  }

  public static void setMemberData(Member.MemberData memberData) {
    getCurrent().MemberData = memberData;
  }

  public static class UserData extends Mapx<String, Object>
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    private String Type;
    private String Status;
    private String UserName;
    private String RealName;
    private String BranchInnerCode;
    private boolean BranchAdminFlag;
    private boolean isLogin = false;
    private String SessionID;
    private Member.MemberData MemberData = new Member.MemberData();

    private String Language = LangMapping.getInstance().getDefaultLanguage();

    private Privilege Priv = new Privilege();

    public String getType() {
      return this.Type;
    }

    public void setType(String type) {
      this.Type = type;
      User.cacheUser(User.getCurrent());
    }

    public String getStatus() {
      return this.Status;
    }

    public void setStatus(String status) {
      this.Status = status;
      User.cacheUser(User.getCurrent());
    }

    public String getUserName() {
      return this.UserName;
    }

    public void setUserName(String userName) {
      this.UserName = userName;
      User.cacheUser(User.getCurrent());
    }

    public String getRealName() {
      return this.RealName;
    }

    public void setRealName(String realName) {
      this.RealName = realName;
      User.cacheUser(User.getCurrent());
    }

    public String getBranchInnerCode() {
      return this.BranchInnerCode;
    }

    public void setBranchInnerCode(String branchInnerCode) {
      this.BranchInnerCode = branchInnerCode;
      User.cacheUser(User.getCurrent());
    }

    public boolean isBranchAdministrator() {
      return this.BranchAdminFlag;
    }

    public void setBranchAdministrator(boolean flag) {
      this.BranchAdminFlag = flag;
      User.cacheUser(User.getCurrent());
    }

    public boolean isLogin() {
      return this.isLogin;
    }

    public void setLogin(boolean isLogin) {
      this.isLogin = isLogin;
      User.cacheUser(User.getCurrent());
    }

    public String getSessionID() {
      return this.SessionID;
    }

    public void setSessionID(String sessionID) {
      this.SessionID = sessionID;
      User.cacheUser(User.getCurrent());
    }

    public String getLanguage() {
      return this.Language;
    }

    public void setLanguage(String language) {
      this.Language = language;
    }

    public Privilege getPrivilege() {
      return this.Priv;
    }

    public void setPrivilege(Privilege priv) {
      this.Priv = priv;
    }

    public Member.MemberData getMemberData() {
      return this.MemberData;
    }

    public void setMemberData(Member.MemberData memberData) {
      this.MemberData = memberData;
    }
  }
}