package com.zving.platform.privilege;

public class BranchPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Branch";
  public static final String Add = "Platform.Branch.Add";
  public static final String Edit = "Platform.Branch.Edit";
  public static final String Delete = "Platform.Branch.Delete";
  public static final String SetPrivRange = "Platform.Branch.SetPrivRange";

  public BranchPriv()
  {
    super("Platform.Branch", null);
    addItem("Platform.Branch.Add", "@{Common.Add}");
    addItem("Platform.Branch.Edit", "@{Common.Edit}");
    addItem("Platform.Branch.Delete", "@{Common.Delete}");
    addItem("Platform.Branch.SetPrivRange", "@{Platform.Branch.SetPrivRange}");
  }
}