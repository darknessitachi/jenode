package com.zving.platform.privilege;

public class RolePriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Role";
  public static final String Add = "Platform.Role.Add";
  public static final String Edit = "Platform.Role.Edit";
  public static final String Delete = "Platform.Role.Delete";
  public static final String SetPriv = "Platform.Role.SetPriv";
  public static final String AddUser = "Platform.Role.AddUser";
  public static final String RemoveUser = "Platform.Role.RemoveUser";

  public RolePriv()
  {
    super("Platform.Role", null);
    addItem("Platform.Role.Add", "@{Common.Add}");
    addItem("Platform.Role.Edit", "@{Common.Edit}");
    addItem("Platform.Role.Delete", "@{Common.Delete}");
    addItem("Platform.Role.SetPriv", "@{Platform.Role.SetPriv}");
    addItem("Platform.Role.AddUser", "@{Role.AddUser}");
    addItem("Platform.Role.RemoveUser", "@{Role.RemoveUser}");
  }
}