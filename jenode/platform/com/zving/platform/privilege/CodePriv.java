package com.zving.platform.privilege;

public class CodePriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Code";
  public static final String Add = "Platform.Code.Add";
  public static final String Edit = "Platform.Code.Edit";
  public static final String Delete = "Platform.Code.Delete";

  public CodePriv()
  {
    super("Platform.Code", null);
    addItem("Platform.Code.Add", "@{Common.Add}");
    addItem("Platform.Code.Edit", "@{Common.Edit}");
    addItem("Platform.Code.Delete", "@{Common.Delete}");
  }
}