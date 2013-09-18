package com.zving.framework;

import com.zving.framework.collection.Mapx;
import java.io.Serializable;

public class Member
{
  public static String getUID()
  {
    return User.getMemberData().UID;
  }

  public static void setUID(String uid) {
    User.getMemberData().UID = uid;
  }

  public static String getUserName()
  {
    return User.getMemberData().UserName;
  }

  public static void setUserName(String username) {
    User.getMemberData().UserName = username;
  }

  public static String getRealName() {
    return User.getMemberData().RealName;
  }

  public static void setRealName(String realName) {
    User.getMemberData().RealName = realName;
  }

  public static String getType() {
    return User.getMemberData().Type;
  }

  public static void setType(String type) {
    User.getMemberData().Type = type;
  }

  public static boolean isLogin() {
    return User.getMemberData().isLogin;
  }

  public static void setLogin(boolean flag) {
    User.getMemberData().isLogin = flag;
  }

  public static Object getValue(String key) {
    return User.getMemberData().get(key);
  }

  public static void setValue(String key, Object value) {
    User.getMemberData().put(key, value);
  }

  public static Mapx<String, Object> getValues() {
    return User.getMemberData();
  }

  public static void destory() {
    User.setMemberData(new MemberData());
  }

  public static MemberData getCurrent() {
    return User.getMemberData();
  }

  public static class MemberData extends Mapx<String, Object>
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    public String UID;
    public String UserName;
    public boolean isLogin;
    public String RealName;
    public String Type;
  }
}