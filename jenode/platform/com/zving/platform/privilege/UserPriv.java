package com.zving.platform.privilege;

public class UserPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.User";
  public static final String Add = "Platform.User.Add";
  public static final String Edit = "Platform.User.Edit";
  public static final String Delete = "Platform.User.Delete";
  public static final String SetPriv = "Platform.User.SetPriv";
  public static final String ChangePassword = "Platform.User.ChangePassword";
  public static final String Disable = "Platform.User.Disable";
  public static final String Enable = "Platform.User.Enable";

  public UserPriv()
  {
    super("Platform.User", null);
    addItem("Platform.User.Add", "@{Common.Add}");
    addItem("Platform.User.Edit", "@{Common.Edit}");
    addItem("Platform.User.Delete", "@{Common.Delete}");
    addItem("Platform.User.SetPriv", "@{Platform.Role.SetPriv}");
    addItem("Platform.User.ChangePassword", "@{Common.ChangePassword}");
    addItem("Platform.User.Disable", "@{User.Disable}");
    addItem("Platform.User.Enable", "@{User.Enable}");
  }
}