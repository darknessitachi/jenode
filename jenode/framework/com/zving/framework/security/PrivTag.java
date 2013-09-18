package com.zving.framework.security;

import java.util.ArrayList;
import java.util.List;

import com.zving.framework.annotation.Priv;
import com.zving.framework.collection.Mapx;
import com.zving.framework.template.AbstractTag;
import com.zving.framework.template.TagAttr;

public class PrivTag extends AbstractTag
{
  private static final long serialVersionUID = 1L;
  private boolean login;
  private String loginType;
  private String userType;
  private String priv;

  public void init()
  {
    this.login = true;
    this.loginType = "User";
    this.userType = "";
    this.priv = "";
  }

  public int doStartTag() {
    Priv.LoginType lt = null;
    if ("User".equals(this.loginType))
      lt = Priv.LoginType.User;
    else {
      lt = Priv.LoginType.Member;
    }
    PrivCheck.check(this.login, lt, this.userType, this.priv, this.pageContext.getVariableResolver());
    return 0;
  }

  public int doEndTag() {
    return 6;
  }

  public boolean isLogin() {
    return this.login;
  }

  public void setLogin(boolean login) {
    this.login = login;
  }

  public String getLoginType() {
    return this.loginType;
  }

  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }

  public String getUserType() {
    return this.userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getPriv() {
    return this.priv;
  }

  public void setPriv(String priv) {
    this.priv = priv;
  }

  public String getPrefix()
  {
    return "z";
  }

  public String getTagName()
  {
    return "priv";
  }

  public List<TagAttr> getTagAttrs()
  {
    List list = new ArrayList();
    list.add(new TagAttr("login", TagAttr.BOOL_OPTIONS));
    Mapx loginTypes = new Mapx();
    loginTypes.put("Member", "Member");
    loginTypes.put("User", "User");
    list.add(new TagAttr("loginType", loginTypes));
    list.add(new TagAttr("priv"));
    list.add(new TagAttr("userType"));
    return list;
  }

  public String getName()
  {
    return "@{Framework.PrivTag.Name}";
  }

  public String getDescription()
  {
    return "@{Framework.PrivTag.Desc}";
  }

  public String getPluginID()
  {
    return "com.zving.framework";
  }
}