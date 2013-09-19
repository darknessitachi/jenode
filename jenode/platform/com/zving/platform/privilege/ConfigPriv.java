package com.zving.platform.privilege;

public class ConfigPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Config";
  public static final String Add = "Platform.Config.Add";
  public static final String Edit = "Platform.Config.Edit";
  public static final String Delete = "Platform.Config.Delete";

  public ConfigPriv()
  {
    super("Platform.Config", null);
    addItem("Platform.Config.Add", "@{Common.Add}");
    addItem("Platform.Config.Edit", "@{Common.Edit}");
    addItem("Platform.Config.Delete", "@{Common.Delete}");
  }
}