package com.zving.platform.privilege;

public class MenuPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Menu";
  public static final String Start = "Platform.Menu.Start";
  public static final String Stop = "Platform.Menu.Stop";
  public static final String Add = "Platform.Menu.Add";
  public static final String Edit = "Platform.Menu.Edit";
  public static final String Del = "Platform.Menu.Delete";
  public static final String Reset = "Platform.Menu.Reset";
  public static final String Sort = "Platform.Menu.Sort";

  public MenuPriv()
  {
    super("Platform.Menu", null);
    addItem("Platform.Menu.Start", "@{Common.Start}");
    addItem("Platform.Menu.Stop", "@{Common.Stop}");
    addItem("Platform.Menu.Add", "@{Menu.NewMenu}");
    addItem("Platform.Menu.Edit", "@{Platform.Menu.Edit}");
    addItem("Platform.Menu.Delete", "@{Common.Delete}");
    addItem("Platform.Menu.Reset", "@{Platform.Menu.Reset}");
    addItem("Platform.Menu.Sort", "@{Platform.Menu.SortMenu}");
  }
}